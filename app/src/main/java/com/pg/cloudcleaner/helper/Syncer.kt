package com.pg.cloudcleaner.helper

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import com.pg.cloudcleaner.utils.StorageHelper
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ReadFileWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    companion object {
        const val KEY_PROGRESS_MESSAGE = "KEY_PROGRESS_MESSAGE"
        const val KEY_PROGRESS = "KEY_PROGRESS"
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
        var lastReportedProgress = -1

        pathsToScan.forEach { path ->
            Timber.d("Starting scan on path: $path")
            fileUseCases.syncAllFilesToDb(path) {
                val processed = processedFiles.incrementAndGet()
                val progress = if (totalFiles > 0) (processed * 100 / totalFiles).coerceIn(0, 100) else 0
                if (progress != lastReportedProgress) {
                    lastReportedProgress = progress
                    setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scanning files...", KEY_PROGRESS to progress))
                }
            }
        }

        val timeTaken = System.currentTimeMillis() - startTime
        Timber.d("Time for filling DB $timeTaken")
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scan finished. Took ${timeTaken / 1000} seconds.", KEY_PROGRESS to 100))

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UpdateChecksumWorker>(
            6, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "checksum-worker",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
        return Result.success()
    }
}
