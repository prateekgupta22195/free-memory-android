package com.pg.cloudcleaner.domain.interactors

import android.text.format.Formatter
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.partialMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.Date

class FileUseCases(private val repo: LocalFilesRepo) {

    suspend fun countFiles(directoryName: String): Int = withContext(Dispatchers.IO) {
        suspend fun countDir(dir: File): Int = coroutineScope {
            val entries = dir.listFiles() ?: return@coroutineScope 0
            var fileCount = 0
            val subdirs = mutableListOf<File>()
            for (entry in entries) {
                when {
                    entry.isFile -> fileCount++
                    entry.isDirectory -> subdirs.add(entry)
                }
            }
            val subCounts = subdirs.map { async { countDir(it) } }.awaitAll()
            fileCount + subCounts.sum()
        }

        val root = File(directoryName)
        if (root.exists() && root.isDirectory) countDir(root) else 0
    }

    suspend fun syncAllFilesToDb(
        directoryName: String,
        onFileProcessed: (suspend () -> Unit)? = null,
    ) = coroutineScope {
        val channel = Channel<List<LocalFile>>(capacity = Channel.BUFFERED)

        // Producer: traverse directories in parallel, compute partial MD5 inline
        launch {
            traverseDir(File(directoryName), channel)
            channel.close()
        }

        // Consumer: each batch is its own Room transaction (no long-lived lock)
        for (batch in channel) {
            repo.insertAll(batch)
            repeat(batch.size) { onFileProcessed?.invoke() }
        }
    }

    private suspend fun traverseDir(dir: File, channel: Channel<List<LocalFile>>) {
        if (!dir.exists() || !dir.isDirectory) return
        val entries = dir.listFiles() ?: return
        val fileList = mutableListOf<File>()
        val subDirs = mutableListOf<File>()
        for (entry in entries) {
            when {
                entry.isFile -> fileList.add(entry)
                entry.isDirectory -> subDirs.add(entry)
            }
        }

        if (fileList.isNotEmpty()) {
            val batch = coroutineScope {
                fileList.map { file ->
                    async(Dispatchers.IO) { file.toLocalFile(duplicate = false, md5 = file.partialMd5()) }
                }.awaitAll()
            }
            channel.send(batch)
        }

        coroutineScope {
            subDirs.forEach { subDir -> launch { traverseDir(subDir, channel) } }
        }
    }

    suspend fun getFileById(fileId: String): LocalFile? {
        return repo.getFileById(fileId)
    }

    suspend fun deleteFile(fileIdentity: String) {
        return repo.deleteFile(fileIdentity)
    }

    suspend fun deleteFiles(ids: List<String>) {
        return repo.deleteFiles(ids)
    }

    fun getMediaFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE 'image/%' OR mimeType LIKE 'video/%' ORDER BY id")
    }

    fun getLargeFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE size > 5000 ORDER BY id")
    }

    fun getVideoFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE 'video/%' ORDER BY id")
    }

    fun getImageFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE 'image/%' ORDER BY id")
    }

    fun getLargeImageFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE 'image/%' AND size > 5000 ORDER BY id")
    }

    fun getDuplicateFileIds(): Flow<List<String>> {
        return repo.getDuplicateFileIds()
    }

    fun getDuplicateMediaFiles(): Flow<Map<String, List<LocalFile>>> {
        return repo.getDuplicateMediaFiles()
            .map { files -> files.groupBy { it.md5CheckSum!! } }
    }

    fun getDuplicateCopies(): Flow<List<LocalFile>> {
        return repo.getDuplicateCopies()
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

}