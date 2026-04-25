package com.pg.cloudcleaner.data.repository

import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.db.dao.LocalFilesDao
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import kotlinx.coroutines.flow.Flow

class LocalFilesRepoImpl(private val dao: LocalFilesDao) : LocalFilesRepo {

    override suspend fun insertFile(localFile: LocalFile) {
        return dao.insert(localFile)
    }

    override suspend fun insertAll(localFiles: List<LocalFile>) {
        localFiles.chunked(BULK_INSERT_CHUNK_SIZE).forEach { chunk ->
            dao.insertAllRaw(buildBulkInsertQuery(chunk))
        }
    }

    private fun buildBulkInsertQuery(files: List<LocalFile>): SimpleSQLiteQuery {
        val sql = buildString {
            append("INSERT OR REPLACE INTO localfile (id, mimeType, modifiedTime, originalFilename, size, md5, isOptimised) VALUES ")
            files.indices.joinTo(this, separator = ",") { "(?,?,?,?,?,?,?)" }
        }
        val args = arrayOfNulls<Any>(files.size * 7)
        files.forEachIndexed { i, file ->
            val base = i * 7
            args[base]     = file.id
            args[base + 1] = file.fileType
            args[base + 2] = file.modifiedTime
            args[base + 3] = file.fileName
            args[base + 4] = file.size
            args[base + 5] = file.md5CheckSum
            args[base + 6] = if (file.isOptimised) 1 else 0
        }
        return SimpleSQLiteQuery(sql, args)
    }

    companion object {
        // floor(999 / 7 columns) — stays within SQLite's SQLITE_MAX_VARIABLE_NUMBER on API 23+
        private const val BULK_INSERT_CHUNK_SIZE = 142
    }

    override fun getAllFiles(): Flow<List<LocalFile>> {
        return dao.getAll()
    }

    override suspend fun deleteFile(id: String) {
        return dao.delete(id)
    }

    override suspend fun deleteFiles(ids: List<String>) {
        App.instance.db.withTransaction {
            ids.forEach { dao.delete(it) }
        }
    }

    override fun getFilesViaQuery(query: String): Flow<List<LocalFile>> {
        return dao.getFilesViaQuery(SimpleSQLiteQuery(query))
    }

    override fun fileAlreadyExists(md5: String): Boolean {
        return dao.fileExists(md5)
    }

    override suspend fun getFileById(id: String): LocalFile? {
        return dao.get(id)
    }

    override fun getDuplicateMediaFiles(): Flow<List<LocalFile>> {
        return dao.getDuplicateMediaFiles()
    }

    override fun getDuplicateCopies(): Flow<List<LocalFile>> {
        return dao.getDuplicateCopies()
    }

    override fun getFilesSizeSumViaQuery(query: String): Flow<Long> {
        return dao.getSumViaQuery(SimpleSQLiteQuery(query))
    }

    override suspend fun applyOptimisationResults(results: List<Pair<String, Long>>) {
        App.instance.db.withTransaction {
            results.forEach { (id, newSizeKb) ->
                dao.updateSizeAndMarkAsOptimised(id, newSizeKb)
            }
        }
    }
}