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

    @Query("DELETE FROM localfile WHERE id IN (:id)")
    fun delete(id: List<String>)

    @Query("SELECT EXISTS(SELECT 1 FROM localfile WHERE id = :id)")
    fun fileExists(id: String): Boolean

    @Query("SELECT id FROM localfile WHERE duplicate = 1 AND (mimeType LIKE '%image%' OR mimeType LIKE '%video%')")
    fun getDuplicateFilesId(): Flow<List<String>>

    @RawQuery(observedEntities = [LocalFile::class])
    fun getSumViaQuery(query: SupportSQLiteQuery): Flow<Long>

    @Query("SELECT * FROM localfile WHERE md5 is NULL LIMIT :limit")
    fun getFilesWithoutChecksum(limit: Int): List<LocalFile>
}