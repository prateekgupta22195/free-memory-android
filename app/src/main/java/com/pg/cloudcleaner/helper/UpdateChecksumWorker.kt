package com.pg.cloudcleaner.helper

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.io.File

class UpdateChecksumWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    companion object {
        private const val BATCH_SIZE = 50
    }

    override suspend fun doWork(): Result {
        val dao = App.instance.db.localFilesDao()

        while (true) {
            Timber.e("Processing a bat files.")

            val filesToUpdate = dao.getFilesWithoutChecksum(BATCH_SIZE)
            Timber.d("Found ${filesToUpdate.size} files to process.")

            if (filesToUpdate.isEmpty()) {
                break // No more files to process
            }

            Timber.d("Processing a batch of ${filesToUpdate.size} files.")

            try {
                coroutineScope {
                    val updatedFiles = filesToUpdate.map {
                        async(Dispatchers.IO) {
                            try {
                                val file = File(it.id)
                                file.toLocalFile(false, file.md5())
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to process file: ${it.id}")
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()

                    if (updatedFiles.isNotEmpty()) {
                        dao.insertAll(updatedFiles)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error processing batch.")
                return Result.retry()
            }
        }

        return Result.success()
    }
}