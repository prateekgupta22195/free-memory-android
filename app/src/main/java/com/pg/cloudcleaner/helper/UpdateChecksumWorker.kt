package com.pg.cloudcleaner.helper

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class UpdateChecksumWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val dao = App.instance.db.localFilesDao()
        val filesToUpdate = dao.getFilesWithoutChecksum()

        Log.e("UpdateChecksumWorker", "Updating checksums for ${filesToUpdate.size} files")
        if (filesToUpdate.isEmpty()) {
            return Result.success()
        }

        try {
            Log.e("UpdateChecksumWorker", "Updating checksums for ${filesToUpdate.size} files")
            val updatedFiles = mutableListOf<LocalFile>()
            for (file in filesToUpdate) {
                withContext(Dispatchers.IO) {
                    try {
                        val file = File(file.id);
                        val md5 = file.md5()
                        val updatedFile = file.toLocalFile(false, md5)
                        updatedFiles.add(updatedFile)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to calculate checksum for: ${file.id}")
                    }
                }
            }
            dao.insertAll(updatedFiles)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update checksums")
            return Result.retry()
        }

        return Result.success()
    }
}