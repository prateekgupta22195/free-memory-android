package com.pg.cloudcleaner.domain.interactors

import android.text.format.Formatter
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import com.pg.cloudcleaner.utils.getMimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File
import java.util.Date

class FileUseCases(private val repo: LocalFilesRepo) {

    suspend fun syncAllFilesToDb(directoryName: String) {
        exploreDirectory(directoryName)
    }

    suspend fun getFileById(fileId: String): LocalFile? {
        return repo.getFileById(fileId)
    }

    suspend fun deleteFile(fileIdentity: String) {
        return repo.deleteFile(fileIdentity)
    }

    fun deleteFiles(ids: List<String>) {
        return repo.deleteFiles(ids)
    }

    fun getMediaFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%' OR mimeType LIKE '%video%'")
    }

    fun getLargeFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE size > 5000")
    }

    fun getVideoFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%video%'")
    }

    fun getImageFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%'")
    }

    fun getDuplicateFileIds(): Flow<List<String>> {
        return repo.getDuplicateFileIds()
    }

    fun getFileInfo(fileId: String): String {

        val file = File(fileId)

        if (!file.exists()) return "-"

        // Use the application context from your App singleton
        val context = App.instance

        // Formatter handles KB, MB, GB logic automatically
        val formattedSize = Formatter.formatFileSize(context, file.length())

        return "File size : $formattedSize" +
                "\nFile Type : ${getMimeType(fileId)}" +
                "\nFile Name : ${file.name}" +
                "\nLocation : $fileId" +
                "\nLast Modified : ${
                    file.lastModified().run {
                        if (this == 0L) "-"
                        else Date(this)
                    }
                }"
    }

    private suspend fun exploreDirectory(directoryPath: String) = coroutineScope {
        // Use a queue for non-recursive directory traversal
        val directoryQueue = ArrayDeque<File>()
        val initialDir = File(directoryPath)

        if (initialDir.exists() && initialDir.isDirectory) {
            directoryQueue.add(initialDir)
        }

        // Process directories from the queue until it's empty
        while (directoryQueue.isNotEmpty()) {
            val currentDir = directoryQueue.removeFirstOrNull() ?: continue
            val files = currentDir.listFiles() ?: continue

            // Use async for CPU-bound tasks (MD5 hashing) and await them in batches
            val fileProcessingJobs = files.filter { it.isFile }.map { file ->
                async(Dispatchers.Default) { // Use Default dispatcher for CPU work
                    try {
                        // Check if file exists and insert in one transaction if possible
                        repo.insertFile(file.toLocalFile(duplicate = false, md5 = null))
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to process file: ${file.absolutePath}")
                    }
                }
            }

            // Add subdirectories to the queue to be processed
            files.filter { it.isDirectory }.forEach { subDir ->
                directoryQueue.add(subDir)
            }

            // Wait for all files in the current directory to finish processing before moving on
            fileProcessingJobs.awaitAll()
        }
    }
}