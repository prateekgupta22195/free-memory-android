package com.pg.cloudcleaner.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import com.pg.cloudcleaner.utils.StorageHelper
import timber.log.Timber


class ReadFileWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    companion object {
        const val KEY_PROGRESS_MESSAGE = "KEY_PROGRESS_MESSAGE"
        const val KEY_PROGRESS = "KEY_PROGRESS"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "scan_channel"
    }

    // Required on API < 31 where WorkManager runs expedited work as a foreground service.
    // On API 31+, WorkManager uses JobScheduler native expedited jobs and never calls this.
    override suspend fun getForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "File Scan", NotificationManager.IMPORTANCE_LOW
            )
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Scanning files")
            .setContentText("Scanning your device for files…")
            .setSmallIcon(R.drawable.baseline_file_present_24)
            .setOngoing(true)
            .build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    override suspend fun doWork(): Result {
        App.instance.db.clearAllTables()
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Starting file scan...", KEY_PROGRESS to 0))
        val startTime = System.currentTimeMillis()
        val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

        val storageHelper = StorageHelper()
        val pathsToScan = storageHelper.getStoragePaths(applicationContext)

        if (pathsToScan.isEmpty()) {
            Timber.e("No storage paths found to scan.")
            setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "No storage paths found to scan.", KEY_PROGRESS to 0))
            return Result.failure()
        }

        // Pass 1: count total files across all paths
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Counting files...", KEY_PROGRESS to 0))
        val totalFiles = pathsToScan.sumOf { fileUseCases.countFiles(it) }
        Timber.d("Total files to scan: $totalFiles")

        // Pass 2: scan and report progress per %
        val processedFiles = java.util.concurrent.atomic.AtomicInteger(0)
        val lastReportedProgress = java.util.concurrent.atomic.AtomicInteger(-1)

        pathsToScan.forEach { path ->
            Timber.d("Starting scan on path: $path")
            fileUseCases.syncAllFilesToDb(path) {
                val processed = processedFiles.incrementAndGet()
                val progress = if (totalFiles > 0) (processed * 100 / totalFiles).coerceIn(0, 100) else 0
                if (lastReportedProgress.getAndSet(progress) != progress) {
                    setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scanning files...", KEY_PROGRESS to progress))
                    kotlinx.coroutines.delay(500)
                }
            }
        }

        val timeTaken = System.currentTimeMillis() - startTime
        Timber.d("Time for filling DB $timeTaken")
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scan finished. Took ${timeTaken / 1000} seconds.", KEY_PROGRESS to 100))

        return Result.success()
    }
}
