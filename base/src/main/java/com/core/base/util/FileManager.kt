package com.core.base.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

class FileManager constructor(val context: Context) {

    /**
     * Creates the File object with path to internal storage (program folder)
     *
     * @param fileName final file name (test.txt|img.jpg|...)
     * @return -> File with path to app folder in inner storage + `fileName`
     *
     * -> NULL, if `fileName` is empty
     */
    fun getInternalFile(fileName: String?): File? {
        if (fileName == null || fileName.trim { it <= ' ' }.isEmpty()) {
            Timber.e("fileName is empty, cant create file.")
            return null
        }
        return File(context.filesDir, fileName)
    }

    /**
     * Creates the File object with path to internal storage (program folder)
     *
     * @param subDirectory additional path from external storage to placing file. Has to start and ends with '/'. (sdCard + subDirectory + fileName).
     * If no need subDirectory set NULL.
     * @param fileName     final file name (test.txt|img.jpg|...)
     * @return -> File with path to app folder in inner storage + `fileName`
     *
     * -> NULL, if any error
     */
    fun getExternalFile(subDirectory: String?, fileName: String?): File? {
        if (fileName == null || fileName.trim { it <= ' ' }.isEmpty()) {
            Timber.e("fileName is empty, cant create file.")
            return null
        }
        if (subDirectory!![0] != '/' || subDirectory[subDirectory.length - 1] != '/') {
            Timber.e("subDirectory is incorrect. It has to start and ends with '/'. subDirectory = $subDirectory")
        }
        val file: File? =
            File(context.getExternalFilesDir(subDirectory), fileName)
        if (file == null) {
            Timber.i("Created file is NULL, please check.")
        }
        return file
    }

    /**
     * Perform saving image to Internal storage
     *
     * @param fileName     output file name
     * @param savingBitmap   bitmap, to save in file
     * @param compressFormat format for saving. [Bitmap.CompressFormat.JPEG] or any other
     * @param quality        quality of saving img, depends on size, as little amt, as small img. ∈ [0,100]. 100 is full size (original).
     */
    fun saveBitmap(
        fileName: String?,
        savingBitmap: Bitmap?,
        compressFormat: Bitmap.CompressFormat?,
        quality: Int
    ) {
        var currentQuality = quality
        if (fileName == null) {
            Timber.e("fileName is NULL, can't save into NULL object")
            return
        }
        if (savingBitmap == null) {
            Timber.e("savingBitmap is NULL, can't save into NULL object")
            return
        }
        if (compressFormat == null) {
            Timber.e("compressFormat is NULL, can't save file with NULL format")
            return
        }
        if (currentQuality < 0 || currentQuality > 100) {
            Timber.e("Invalid quality, it has to be in ∈ [0,100]. It will be set to 100. quality = $quality")
            currentQuality = 100
        }

        val file = getInternalFile(fileName)
        try {
            val os = FileOutputStream(file!!)
            savingBitmap.compress(compressFormat, currentQuality, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Timber.e("Error on saving file. ${e.message}")
        }

    }

    /**
     * Reads Bitmap from Internal storage
     * @param name Bitmap name
     * @return Bitmap
     */
    fun readBitmap(name: String): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.openFileInput(name)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return BitmapFactory.decodeStream(inputStream)
    }


    /**
     * Deletes File from Internal storage
     *
     * @param fileName File name
     * @return true if deleted, false otherwise
     */
    fun deleteFile(fileName: String): Boolean {
        val file = getInternalFile(fileName) ?: return false
        return file.delete()
    }

    /**
     * Delete file from storage
     *
     * @param file file to delete. Full path to file
     */
    fun deleteFile(file: File?) {
        if (file == null) {
            Timber.e("file is NULL, can't delete (find) NULL object")
            return
        }
        if (file.exists()) {
            val isDeleted = file.delete()
            if (!isDeleted) {
                Timber.i("Can't delete file in some reason.")
            }
        } else {
            Timber.i("Can't find file to delete. file = ${file.absolutePath}")
        }
    }
}