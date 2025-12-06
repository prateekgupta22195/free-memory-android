package com.pg.cloudcleaner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pg.cloudcleaner.data.db.dao.LocalFilesDao
import com.pg.cloudcleaner.data.model.LocalFile


@Database(entities = [LocalFile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localFilesDao(): LocalFilesDao
}


