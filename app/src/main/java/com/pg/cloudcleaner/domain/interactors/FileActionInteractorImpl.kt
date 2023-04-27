package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import com.pg.cloudcleaner.utils.calculateMD5
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.Date
import kotlin.math.round

class FileActionInteractorImpl(private val repo: LocalFilesRepo) : FileActionInteractor {

    override suspend fun syncAllFilesToDb(directoryName: String) {
        exploreDirectory(directoryName)
    }

    override fun getAllFiles(): Flow<List<LocalFile>> {
        return repo.getAllFiles()
    }

    override suspend fun getFileById(fileId: String): LocalFile? {
        return repo.getFileById(fileId)
    }

    override suspend fun deleteFile(fileIdentity: String) {
        return repo.deleteFile(fileIdentity)
    }

    override fun deleteFiles(ids: List<String>) {
        return repo.deleteFiles(ids)
    }

    override fun getMediaFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%' OR mimeType LIKE '%video%'")
    }

    override fun getVideoFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%video%'")
    }

    override fun getImageFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%'")
    }

    override fun getDuplicateFileIds(): Flow<List<String>> {
        return repo.getDuplicateFileIds()
    }

    override fun getFileInfo(fileId: String): String {

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
                            calculateMD5(file)
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