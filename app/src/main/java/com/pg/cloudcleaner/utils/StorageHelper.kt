package com.pg.cloudcleaner.utils

import android.app.usage.StorageStatsManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.core.content.getSystemService
import com.pg.cloudcleaner.app.App
import timber.log.Timber
import java.io.IOException
import java.util.UUID

// Data class to hold storage information
data class StorageInfo(
    val totalSpaceGB: Float,
    val usedSpaceGB: Float,
    val freeSpaceGB: Float
)

class StorageHelper {

    val context = App.instance.applicationContext

    /**
     * Calculates the total, used, and free storage space of the device,
     * combining both internal and primary external storage.
     */
    fun getTotalStorageInfo(): StorageInfo {
        // Fallback to the old method for older Android versions

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return getLegacyStorageInfo()
        }

        val storageManager = context.getSystemService<StorageManager>()
        val storageStatsManager = context.getSystemService<StorageStatsManager>()

        if (storageManager == null || storageStatsManager == null) {
            // If services are unavailable, fallback to the simpler method
            return getLegacyStorageInfo()
        }

        var totalBytes = 0L
        var freeBytes = 0L

        // Iterate over all storage volumes, including internal and any SD cards
        for (storageVolume in storageManager.storageVolumes) {
            // THE FIX IS HERE:
            // Before parsing, check if the UUID string is valid and not a placeholder.
            val uuid: UUID = if (storageVolume.uuid != null && storageVolume.uuid != "0000-0000") {
                UUID.fromString(storageVolume.uuid)
            } else {
                StorageManager.UUID_DEFAULT
            }

            try {
                totalBytes += storageStatsManager.getTotalBytes(uuid)
                freeBytes += storageStatsManager.getFreeBytes(uuid)
            } catch (e: IOException) {
                // This can happen if the volume is not mounted or accessible.
                // It's safe to just continue to the next volume.
                e.printStackTrace()
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
     * A fallback method for older Android versions or when StorageStatsManager is not available.
     * This method primarily measures the internal data partition.
     */
    private fun getLegacyStorageInfo(): StorageInfo {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalSpaceGB = (totalBlocks * blockSize) / (1024f * 1024 * 1024)
        val freeSpaceGB = (availableBlocks * blockSize) / (1024f * 1024 * 1024)
        val usedSpaceGB = totalSpaceGB - freeSpaceGB

        return StorageInfo(totalSpaceGB, usedSpaceGB, freeSpaceGB)
    }
}
