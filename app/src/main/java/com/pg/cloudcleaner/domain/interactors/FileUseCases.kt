package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.md5
import com.pg.cloudcleaner.utils.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.Date
import kotlin.math.round

class FileUseCases(private val repo: LocalFilesRepo) {

    suspend fun syncAllFilesToDb(directoryName: String) {
        exploreDirectory(directoryName)
    }

    fun getAllFiles(): Flow<List<LocalFile>> {
        return repo.getAllFiles()
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

        val fileSize = file.size()
        val fileSizeString: String = if (fileSize > 1000) {
            "File size : ${(round((fileSize / 1000).toDouble()))} MB"
        } else {
            "File size : ${(round((fileSize).toDouble()))} KB"
        }
        return fileSizeString + "\nFile Type : ${getMimeType(fileId)}" + "\nFile Name : ${file.name}" + "\nLocation : $fileId" + "\nLast Modified : ${
            file.lastModified().run {
                if (this == 0L) "-"
                else Date(this)
            }
        }"
    }

    private suspend fun exploreDirectory(directoryPath: String) {
        withContext(Dispatchers.IO) {
            val directory = File(directoryPath)
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    Timber.d("directory name ${file.absolutePath}")
                    exploreDirectory(file.absolutePath)
                } else {
                    Timber.d("file name ${file.absolutePath}")
                    withContext(Dispatchers.Default) {
                        val md5 = try {
                            file.md5()
                        } catch (e: Exception) {
                            ""
                        }
                        repo.insertFile(file.toLocalFile(repo.fileAlreadyExists(md5), md5))
                    }

                }

            }
        }
    }


}