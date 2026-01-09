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
    }

    override suspend fun doWork(): Result {
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Starting file scan..."))
        val startTime = System.currentTimeMillis()
        val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

        val storageHelper = StorageHelper()
        val pathsToScan = storageHelper.getStoragePaths(applicationContext)

        if (pathsToScan.isEmpty()) {
            Timber.e("No storage paths found to scan.")
            setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "No storage paths found to scan."))
            return Result.failure()
        }

        // Scan each storage volume found.
        pathsToScan.forEach { path ->
            Timber.d("Starting scan on path: $path")
            setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scanning: $path"))
            fileUseCases.syncAllFilesToDb(path)
        }

        val timeTaken = System.currentTimeMillis() - startTime
        Timber.d("Time for filling DB $timeTaken")
        setProgress(workDataOf(KEY_PROGRESS_MESSAGE to "Scan finished. Took ${timeTaken / 1000} seconds."))

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
