package com.pg.cloudcleaner.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.pg.cloudcleaner.data.db.dao.LocalFilesDao
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import kotlinx.coroutines.flow.Flow

class LocalFilesRepoImpl(private val dao: LocalFilesDao) : LocalFilesRepo {

    override suspend fun insertFile(localFile: LocalFile) {
        return dao.insert(localFile)
    }

    override suspend fun insertAll(localFiles: List<LocalFile>) {
        return dao.insertAll(localFiles)
    }

    override fun getAllFiles(): Flow<List<LocalFile>> {
        return dao.getAll()
    }

    override fun deleteFile(id: String) {
        return dao.delete(id)
    }

    override fun deleteFiles(ids: List<String>) {
        return dao.delete(ids)
    }

    override fun getFilesViaQuery(query: String): Flow<List<LocalFile>> {
        return dao.getFilesViaQuery(SimpleSQLiteQuery(query))
    }

    override fun getFilesIDLike(id: String): Flow<List<LocalFile>> {
        return dao.getByIdLike(id)
    }

    override fun fileAlreadyExists(md5: String): Boolean {
        return dao.fileExists(md5)
    }

    override suspend fun getFileById(id: String): LocalFile? {
        return dao.get(id)
    }

}