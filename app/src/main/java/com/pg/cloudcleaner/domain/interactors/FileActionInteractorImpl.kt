package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.model.toLocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import com.pg.cloudcleaner.utils.getMimeType
import com.pg.cloudcleaner.utils.size
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors

class FileActionInteractorImpl(private val repo: LocalFilesRepo) : FileActionInteractor {

    val threadPool = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    override suspend fun syncAllFilesToDb(directoryName: String) {
        exploreDirectory(directoryName)
    }

    override fun getAllFiles(): Flow<List<LocalFile>> {
        return repo.getAllFiles()
    }

    override fun deleteFile(fileIdentity: String) {
        return repo.deleteFile(fileIdentity)
    }

    override fun getMediaFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%' OR mimeType LIKE '%video%'")
    }

    private suspend fun exploreDirectory(directoryPath: String) {
        withContext(Dispatchers.IO) {
            val directory = File(directoryPath)
            launch(threadPool) {
                directory.listFiles()?.forEach { file ->
                    if (file.isDirectory)
                        exploreDirectory(file.absolutePath)
                    else {


                        Timber.d("file size ${file.size()}")

                        if (getMimeType(file.absolutePath)?.contains("image") == true || getMimeType(
                                file.absolutePath
                            )?.contains(
                                "video"
                            ) == true
                        )
                            repo.insertFile(file.toLocalFile())
                    }

                }
            }
        }
    }


}