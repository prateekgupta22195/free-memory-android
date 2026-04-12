package com.pg.cloudcleaner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pg.cloudcleaner.data.db.dao.LocalFilesDao
import com.pg.cloudcleaner.data.model.LocalFile

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE localfile ADD COLUMN isOptimised INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [LocalFile::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localFilesDao(): LocalFilesDao
}


