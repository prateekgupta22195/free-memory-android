package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HomeUseCases(private val repo: LocalFilesRepo) {

    fun getAnyThreeDuplicateGroups(): Flow<List<LocalFile>> {
        // Get the first copy of each duplicate group (same EXISTS pattern as getDuplicatesCount)
        // - NOT EXISTS(...id < f1.id): this is the lowest-id file for its md5 (the "original")
        // - EXISTS(...id != f1.id): at least one other file shares the same md5 (confirmed duplicate)
        val query = "SELECT * FROM localfile f1 " +
                "WHERE f1.mimeType LIKE 'image/%' " +
                "AND NOT EXISTS (SELECT 1 FROM localfile f2 WHERE f2.md5 = f1.md5 AND f2.id < f1.id) " +
                "AND EXISTS (SELECT 1 FROM localfile f2 WHERE f2.md5 = f1.md5 AND f2.id != f1.id) " +
                "ORDER BY f1.id " +
                "LIMIT 3"
        return repo.getFilesViaQuery(query).flowOn(Dispatchers.IO)
    }

    fun getVideoFile(): Flow<LocalFile?> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE 'video/%' limit 2")
            .flowOn(Dispatchers.IO).map {
                if (it.isEmpty()) null
                else it[0]
            }
    }

    fun getNVideoFiles(limit: Int?=null): Flow<List<LocalFile>> {
        val limitClause = if (limit != null) " LIMIT $limit" else ""
        val query = "SELECT * FROM localfile WHERE mimeType LIKE 'video/%' ORDER BY id$limitClause"
        return repo.getFilesViaQuery(query).flowOn(Dispatchers.IO)
    }

    fun getImageFiles(limit: Int? = null): Flow<List<LocalFile>> {
        val limitClause = if (limit != null) " LIMIT $limit" else ""
        val query = "SELECT * FROM localfile WHERE mimeType LIKE 'image/%' ORDER BY id$limitClause"
        return repo.getFilesViaQuery(query).flowOn(Dispatchers.IO)
    }

    fun getLargeFiles(limit: Int? = null): Flow<List<LocalFile>> {
        val limitClause = if (limit != null) " LIMIT $limit" else ""
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE size > 5000 ORDER BY id$limitClause").flowOn(Dispatchers.IO)
    }

    fun getTotalSizeOfMimeType(mimeTypePattern: String): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE mimeType LIKE '$mimeTypePattern'"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    // 2. Get sum for Large Files (Size > 5000MB)
    fun getTotalSizeOfLargeFiles(): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE size > 5000"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    // 3. Get sum for specific MimeType AND minimum size (Dynamic)
    fun getCustomTotalSize(mimeTypePattern: String, minSize: Long): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE mimeType LIKE '$mimeTypePattern' AND size > $minSize"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    // COUNT queries — efficient, no full list loaded into memory
    fun getTotalSizeOfDuplicates(): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile f1 " +
                "WHERE f1.mimeType LIKE 'image/%' " +
                "AND EXISTS (SELECT 1 FROM localfile f2 " +
                "WHERE f2.md5 = f1.md5 AND f2.id < f1.id)"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    fun getDuplicatesCount(): Flow<Int> {
        val query = "SELECT COUNT(*) FROM localfile f1 " +
                "WHERE f1.mimeType LIKE 'image/%' " +
                "AND EXISTS (SELECT 1 FROM localfile f2 " +
                "WHERE f2.md5 = f1.md5 AND f2.id < f1.id)"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO).map { it.toInt() }
    }

    fun getImagesCount(): Flow<Int> {
        return repo.getFilesSizeSumViaQuery("SELECT COUNT(*) FROM localfile WHERE mimeType LIKE 'image/%'")
            .flowOn(Dispatchers.IO).map { it.toInt() }
    }

    fun getVideosCount(): Flow<Int> {
        return repo.getFilesSizeSumViaQuery("SELECT COUNT(*) FROM localfile WHERE mimeType LIKE 'video/%'")
            .flowOn(Dispatchers.IO).map { it.toInt() }
    }

    fun getLargeFilesCount(): Flow<Int> {
        return repo.getFilesSizeSumViaQuery("SELECT COUNT(*) FROM localfile WHERE size > 5000")
            .flowOn(Dispatchers.IO).map { it.toInt() }
    }
}