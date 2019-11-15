package com.core.base.util

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import androidx.fragment.app.Fragment
import com.mindorks.paracamera.Camera
import timber.log.Timber

class CameraManager {

    private var camera: Camera? = null

    interface OnCameraErrorListener {
        fun onCameraError(error: String)
    }

    fun requestPermissions(context: Fragment, permissionRequestCode: Int) {
        context.requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            permissionRequestCode
        )
    }

    /**
     * Open camera to take photo.
     * Created photo will be return to `activity` to onActivityResult method.
     *
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun takePhoto(activity: Activity, listener: OnCameraErrorListener) {
        // Build the camera
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
            .setTakePhotoRequestCode(1)
            .setDirectory("pics")
            .setName("ali_" + System.currentTimeMillis())
            .setImageFormat(Camera.IMAGE_JPEG)
            .setCompression(70)
            .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
            .build(activity)
        // Call the camera takePicture method to show the existing camera
        try {
            camera!!.takePicture()
        } catch (e: Exception) {
            Timber.e("Error on opening camera. ${e.message}")
            listener.onCameraError(e.message ?: e.toString())
        }
    }

    /**
     * Returns Bitmap shot on Camera
     */
    fun getCameraBitmap(): Bitmap? {
        return camera?.cameraBitmap
    }
}


