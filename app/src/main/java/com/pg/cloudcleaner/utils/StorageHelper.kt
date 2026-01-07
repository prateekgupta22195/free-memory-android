package com.pg.cloudcleaner.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.core.content.getSystemService
import com.pg.cloudcleaner.app.App
import timber.log.Timber

// Data class to hold storage information
data class StorageInfo(
    val totalSpaceGB: Float,
    val usedSpaceGB: Float,
    val freeSpaceGB: Float
)

class StorageHelper {

    val context: Context = App.instance.applicationContext

    /**
     * Calculates the total, used, and free storage space of the device,
     * combining all available storage volumes.
     */
    fun getTotalStorageInfo(): StorageInfo {
        var totalBytes = 0L
        var freeBytes = 0L

        val paths = getStoragePaths(context)

        if (paths.isEmpty()) {
            Timber.w("getStoragePaths returned empty list, falling back to legacy method.")
            return getLegacyStorageInfo()
        }

        for (path in paths) {
            try {
                val stat = StatFs(path)
                totalBytes += stat.totalBytes
                freeBytes += stat.availableBytes
            } catch (e: Exception) {
                Timber.e(e, "Error getting stats for path: $path")
                // If one path fails, continue with others.
            }
        }

        val usedBytes = totalBytes - freeBytes

        // Convert to Gigabytes
        val totalSpaceGB = totalBytes / (1024f * 1024 * 1024)
        val freeSpaceGB = freeBytes / (1024f * 1024 * 1024)
        val usedSpaceGB = usedBytes / (1024f * 1024 * 1024)

        return StorageInfo(
            totalSpaceGB = totalSpaceGB,
            usedSpaceGB = usedSpaceGB,
            freeSpaceGB = freeSpaceGB
        )
    }

    /**
     * Gets a list of all readable, primary storage paths, including internal and SD cards.
     * This logic is the single source of truth for storage volumes.
     */
    fun getStoragePaths(context: Context): List<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            val externalStorage = Environment.getExternalStorageDirectory()
            return if (externalStorage != null) listOf(externalStorage.absolutePath) else emptyList()
        }

        val storageManager = context.getSystemService<StorageManager>()
        val storageVolumes = storageManager?.storageVolumes ?: return emptyList()

        return storageVolumes.mapNotNull { volume ->
            if (volume.state != Environment.MEDIA_MOUNTED && volume.state != Environment.MEDIA_MOUNTED_READ_ONLY) {
                return@mapNotNull null
            }

            if (volume.isPrimary) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // On Android 11+, getDirectory() is the correct way.
                    volume.directory?.absolutePath
                } else {
                    // On older versions, a hacky reflection method might be needed for the full path,
                    // but we can construct a reliable path for primary storage.
                    // This will correctly point to /storage/emulated/0
                    @Suppress("DEPRECATION")
                    Environment.getExternalStorageDirectory().absolutePath
                }
            } else if (volume.isRemovable) {
                // This handles removable SD cards
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    volume.directory?.absolutePath
                } else {
                    // On older versions, getting the SD card path is notoriously tricky.
                    // This reflection method is a common and effective workaround.
                    try {
                        val pathField = volume.javaClass.getDeclaredField("mPath")
                        pathField.isAccessible = true
                        pathField.get(volume) as? String
                    } catch (e: Exception) {
                        Timber.e(e, "Error getting SD card path via reflection")
                        null
                    }
                }
            } else {
                null
            }
        }
    }

    /**
     * A fallback method for older Android versions or when storage volumes can't be listed.
     * This method measures the primary external storage.
     */
    private fun getLegacyStorageInfo(): StorageInfo {
        return try {
            @Suppress("DEPRECATION")
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val totalBytes = stat.totalBytes
            val freeBytes = stat.availableBytes
            val usedBytes = totalBytes - freeBytes

            val totalSpaceGB = totalBytes / (1024f * 1024 * 1024)
            val freeSpaceGB = freeBytes / (1024f * 1024 * 1024)
            val usedSpaceGB = usedBytes / (1024f * 1024 * 1024)

            StorageInfo(totalSpaceGB, usedSpaceGB, freeSpaceGB)
        } catch (e: Exception) {
            Timber.e(e, "Failed to get legacy storage info.")
            StorageInfo(0f, 0f, 0f)
        }
    }
}
