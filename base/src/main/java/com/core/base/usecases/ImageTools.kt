package com.core.base.usecases

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import timber.log.Timber
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Tools to create Map Icons for restaurants etc
 */
class ImageTools {
    companion object {
        // Difference between background and image to nicely fit image in circular background
        private const val IMAGE_TO_BG_COEFFICIENT = 1.3F

        /**
         * Makes single Bitmap from background and foreground image
         */
        fun makeMapIconOverlay(background: Bitmap, image: Bitmap): Bitmap? {
            val widthBackground = background.width
            val heightBackground = background.height
            val widthImage = image.width
            val heightImage = image.height
            val ratio =
                background.width.toFloat() / image.width.toFloat()
            val resized: Bitmap? = getResizedBitmap(
                image,
                (widthImage * ratio / IMAGE_TO_BG_COEFFICIENT).toInt(),
                (heightImage * ratio / IMAGE_TO_BG_COEFFICIENT).toInt()
            )
            val bmOverlay =
                Bitmap.createBitmap(widthBackground, heightBackground, background.config)
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(background, Matrix(), null)
            canvas.drawBitmap(
                resized!!,
                -(resized.width * (1f - IMAGE_TO_BG_COEFFICIENT)) / 2f,
                resized.height / 2f,
                null
            )
            return bmOverlay
        }

        fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // Create matrix
            val matrix = Matrix()
            // Resize bitmap
            matrix.postScale(scaleWidth, scaleHeight)
            // Make final bitmap
            val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false
            )
            bm.recycle()
            return resizedBitmap
        }

        /**
         * Perform resize of bitmap. Scale proportional, so img save it's shape. Resize only square img, without getHeight (other shape will have outSize as biggest side). Img size will multiply with a coefficient of each side.
         * Coefficient is calculating as min(`outSize`-`incomeImg.getWidth`;`outSize`-`incomeImg.getHeight`)
         *
         * @param incomeImg bitmap for transformation
         * @param outSize   size of output bitmap
         * @return -> transformed bitmap to `outSize` size
         *
         * -> income bitmap, if `outSize` = income bitmap size (width or height)
         *
         * -> NULL, if any error
         */
        fun resizeImageProportional(incomeImg: Bitmap?, outSize: Float): Bitmap? {
            if (incomeImg == null) {
                Timber.e("incomeImg is NULL, can't operate with NULL object.")
                return null
            }
            if (outSize == incomeImg.width.toFloat() || outSize == incomeImg.height.toFloat()) {
                Timber.i("No transformation performed. outSize is the same as income img. outSize = $outSize incomeImg.getWidth() = ${incomeImg.width} incomeImg.getHeight() = ${incomeImg.height}")
                return incomeImg
            }
            val ratio = min(
                outSize / incomeImg.width,
                outSize / incomeImg.height
            )
            val width = (ratio * incomeImg.width).roundToInt()
            val height = (ratio * incomeImg.height).roundToInt()
            val newBitmap = Bitmap.createScaledBitmap(
                incomeImg, width,
                height, true
            )
            if (newBitmap == null) {
                Timber.e("Resized bitmap is NULL, please check.")
            }
            return newBitmap
        }
    }
}
