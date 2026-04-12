package com.pg.cloudcleaner.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

object ImageOptimizer {

    const val QUALITY = 80
    internal const val EXIF_MARKER = "cc_optimised"

    // Returns bytes saved (original − new size), or 0 if optimization failed / not beneficial.
    fun optimize(filePath: String): Long {
        val file = File(filePath)
        if (!file.exists()) return 0L
        val originalSize = file.length()

        val tempFile = File("$filePath.tmp")
        return try {
            val bitmap = BitmapFactory.decodeFile(filePath) ?: return 0L
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, out)
            }
            bitmap.recycle()

            val newSize = tempFile.length()
            return if (newSize < originalSize) {
                tempFile.renameTo(file)
                markAsOptimised(filePath)
                originalSize - newSize
            } else {
                tempFile.delete()
                markAsOptimised(filePath)
                0L
            }
        } catch (_: OutOfMemoryError) {
            Timber.w("OOM optimizing $filePath, skipping")
            tempFile.delete()
            0L
        } catch (e: Exception) {
            Timber.e(e, "Failed to optimize $filePath")
            tempFile.delete()
            0L
        }
    }

    private fun markAsOptimised(filePath: String) {
        try {
            val exif = ExifInterface(filePath)
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, EXIF_MARKER)
            exif.saveAttributes()
        } catch (e: Exception) {
            Timber.w(e, "Failed to write optimised marker to $filePath")
        }
    }

    // Rough estimate: JPEG at 80% quality saves ~50% for typical photos.
    fun estimatedSavings(sizeBytes: Long): Long = (sizeBytes * 0.5).toLong()
}
