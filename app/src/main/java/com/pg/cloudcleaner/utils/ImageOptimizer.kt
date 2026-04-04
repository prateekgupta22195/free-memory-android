package com.pg.cloudcleaner.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

object ImageOptimizer {

    const val QUALITY = 80

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
            if (newSize in 1 until originalSize) {
                tempFile.renameTo(file)
                originalSize - newSize
            } else {
                tempFile.delete()
                0L
            }
        } catch (e: OutOfMemoryError) {
            Timber.w("OOM optimizing $filePath, skipping")
            tempFile.delete()
            0L
        } catch (e: Exception) {
            Timber.e(e, "Failed to optimize $filePath")
            tempFile.delete()
            0L
        }
    }

    // Rough estimate: JPEG at 80% quality saves ~50% for typical photos.
    fun estimatedSavings(sizeBytes: Long): Long = (sizeBytes * 0.5).toLong()
}
