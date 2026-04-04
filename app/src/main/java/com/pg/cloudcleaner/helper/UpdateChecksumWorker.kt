package com.pg.cloudcleaner.helper

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CancellationException
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
            Timber.d("Processing batch files.")

            val filesToUpdate = dao.getFilesWithoutChecksum(BATCH_SIZE)
            Timber.d("Found ${filesToUpdate.size} files to process.")

            if (filesToUpdate.isEmpty()) {
                break // No more files to process
            }

            Timber.d("Processing a batch of ${filesToUpdate.size} files.")

            try {
                coroutineScope {
                    val updates = filesToUpdate.map { localFile ->
                        async(Dispatchers.IO) {
                            Pair(localFile.id, File(localFile.id).md5() ?: "")
                        }
                    }.awaitAll()

                    App.instance.db.withTransaction {
                        updates.forEach { (id, md5) -> dao.updateMd5(id, md5) }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error processing batch.")
                return Result.retry()
            }
        }

        return Result.success()
    }
}