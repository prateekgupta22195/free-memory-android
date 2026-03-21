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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.Date

class FileUseCases(private val repo: LocalFilesRepo) {

    suspend fun countFiles(directoryName: String): Int = coroutineScope {
        suspend fun countDir(dir: File): Int = withContext(Dispatchers.IO) {
            val entries = dir.listFiles() ?: return@withContext 0
            val fileCount = entries.count { it.isFile }
            val subdirs = entries.filter { it.isDirectory }

            if (subdirs.isEmpty()) return@withContext fileCount

            // Process subdirectories in parallel
            val subCounts = subdirs.map { subdir ->
                async { countDir(subdir) }
            }.awaitAll()

            fileCount + subCounts.sum()
        }

        val root = File(directoryName)
        if (root.exists() && root.isDirectory) {
            countDir(root)
        } else 0
    }

    suspend fun syncAllFilesToDb(
        directoryName: String,
        onFileProcessed: (suspend () -> Unit)? = null,
    ) {
        exploreDirectory(directoryName, onFileProcessed)
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

    suspend fun getFilesByCategory(category: String): List<LocalFile> {
        return when (category) {
            "category_images" -> getImageFiles().first()
            "category_videos" -> getVideoFiles().first()
            "category_large_files" -> getLargeFiles().first()
            "category_duplicates" -> {
                val duplicateIds = getDuplicateFileIds().first()
                duplicateIds.mapNotNull { getFileById(it) }
            }
            else -> emptyList()
        }
    }

    suspend fun getFilesByMd5(md5: String): List<LocalFile> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE md5 = '$md5'").first()
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

    private suspend fun exploreDirectory(
        directoryPath: String,
        onFileProcessed: (suspend () -> Unit)? = null,
    ) = coroutineScope {
        val directoryQueue = ArrayDeque<File>()
        val initialDir = File(directoryPath)

        if (initialDir.exists() && initialDir.isDirectory) {
            directoryQueue.add(initialDir)
        }

        while (directoryQueue.isNotEmpty()) {
            val currentDir = directoryQueue.removeFirstOrNull() ?: continue
            val files = currentDir.listFiles() ?: continue

            val fileProcessingJobs = files.filter { it.isFile }.map { file ->
                async(Dispatchers.Default) {
                    try {
                        repo.insertFile(file.toLocalFile(duplicate = false, md5 = null))
                        onFileProcessed?.invoke()
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to process file: ${file.absolutePath}")
                    }
                }
            }

            files.filter { it.isDirectory }.forEach { subDir ->
                directoryQueue.add(subDir)
            }

            fileProcessingJobs.awaitAll()
        }
    }
}