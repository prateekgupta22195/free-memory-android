package com.pg.cloudcleaner.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import timber.log.Timber


class ReadFileWorker(context: Context, workerParameters: WorkerParameters) :

    CoroutineWorker(context, workerParameters), WorkerNotification {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "11"
        private const val NOTIFICATION_CHANNEL_NAME = "Work Service"
    }

    override suspend fun doWork(): Result {
        val startTime = System.currentTimeMillis()
        val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

        // --- START OF FIX ---
        // Get all storage volumes to scan, including SD cards.
        val pathsToScan = getStoragePaths(applicationContext)

        if (pathsToScan.isEmpty()) {
            Timber.e("No storage paths found to scan.")
            return Result.failure() // Or success, if no paths is not an error state.
        }

        // Scan each storage volume found.
        pathsToScan.forEach { path ->
            Timber.d("Starting scan on path: $path")
            fileUseCases.syncAllFilesToDb(path)
        }
        // --- END OF FIX ---

        Timber.d("Time for filling DB ${System.currentTimeMillis() - startTime}")
        return Result.success()
    }

    /**
     * Gets a list of all readable, primary storage paths, including internal and SD cards.
     */
    private fun getStoragePaths(context: Context): List<String> {
        val storageManager = context.getSystemService<StorageManager>()
        val storageVolumes = storageManager?.storageVolumes ?: return emptyList()

        return storageVolumes.mapNotNull { volume ->
            // isPrimary indicates it's a main storage device (not some system partition).
            // isRemovable helps distinguish between internal and SD card.
            // We need to check its state to ensure it's mounted and readable.
            if (volume.isPrimary && volume.state == Environment.MEDIA_MOUNTED) {
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
            } else if (volume.isRemovable && volume.state == Environment.MEDIA_MOUNTED) {
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
            }
            else {
                null
            }
        }
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            System.currentTimeMillis().toInt(), setForegroundNotification()
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannel() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }


    override fun getNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true).setAutoCancel(true)
            .setProgress(100, 0, true).setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle(applicationContext.getString(R.string.app_name)).setLocalOnly(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET).setContentText("Scanning Files")
            .build()
    }

    override fun setForegroundNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return getNotification()
    }

}
