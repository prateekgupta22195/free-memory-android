package com.pg.cloudcleaner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pg.cloudcleaner.model.DriveFile
import kotlinx.coroutines.flow.Flow

@Dao
interface DriveFileDao {

    @Query("SELECT * FROM drivefile")
    fun getAll(): Flow<List<DriveFile>>

    @Query("SELECT * FROM drivefile WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flow<List<DriveFile>>

    @Query("SELECT * FROM drivefile WHERE id == (:id)")
    fun get(id: String): Flow<DriveFile?>

    @Query("SELECT * FROM drivefile WHERE mimeType LIKE '%' || :mimeType || '%' ")
    fun findByName(mimeType: String): Flow<List<DriveFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<DriveFile>)

    @Delete
    suspend fun delete(file: DriveFile)

    @Query("DELETE FROM drivefile WHERE id == (:id)")
    suspend fun delete(id: String)
}
