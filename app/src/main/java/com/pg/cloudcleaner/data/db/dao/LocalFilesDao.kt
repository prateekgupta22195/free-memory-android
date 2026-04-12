package com.pg.cloudcleaner.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalFilesDao {

    @Query("SELECT * FROM localfile")
    fun getAll(): Flow<List<LocalFile>>

    @Query("SELECT * FROM localfile WHERE id = :id")
    suspend fun get(id: String): LocalFile?

    @RawQuery(observedEntities = [LocalFile::class])
    fun getFilesViaQuery(query: SupportSQLiteQuery): Flow<List<LocalFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<LocalFile>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: LocalFile)

    @Delete
    suspend fun delete(file: LocalFile)

    @Query("DELETE FROM localfile WHERE id = :id")
    fun delete(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM localfile WHERE id = :id)")
    fun fileExists(id: String): Boolean

    @Query("SELECT id FROM localfile WHERE duplicate = 1 AND (mimeType LIKE 'image/%' OR mimeType LIKE 'video/%')")
    fun getDuplicateFilesId(): Flow<List<String>>

    @RawQuery(observedEntities = [LocalFile::class])
    fun getSumViaQuery(query: SupportSQLiteQuery): Flow<Long>

    @Query("SELECT * FROM localfile WHERE md5 is NULL LIMIT :limit")
    fun getFilesWithoutChecksum(limit: Int): List<LocalFile>

    @Query("SELECT * FROM localfile WHERE (mimeType LIKE 'image/%' OR mimeType LIKE 'video/%') AND md5 IS NOT NULL AND md5 IN (SELECT md5 FROM localfile WHERE (mimeType LIKE 'image/%' OR mimeType LIKE 'video/%') AND md5 IS NOT NULL GROUP BY md5 HAVING COUNT(*) > 1) ORDER BY md5, id")
    fun getDuplicateMediaFiles(): Flow<List<LocalFile>>

    @Query("SELECT * FROM localfile f1 WHERE (f1.mimeType LIKE 'image/%' OR f1.mimeType LIKE 'video/%') AND f1.md5 IS NOT NULL AND EXISTS (SELECT 1 FROM localfile f2 WHERE f2.md5 = f1.md5 AND f2.id < f1.id) ORDER BY f1.md5, f1.id")
    fun getDuplicateCopies(): Flow<List<LocalFile>>

    @Query("UPDATE localfile SET md5 = :md5 WHERE id = :id")
    suspend fun updateMd5(id: String, md5: String)

    @Query("UPDATE localfile SET size = :size WHERE id = :id")
    suspend fun updateSize(id: String, size: Long)

    @Query("UPDATE localfile SET isOptimised = 1 WHERE id = :id")
    suspend fun markAsOptimised(id: String)
}