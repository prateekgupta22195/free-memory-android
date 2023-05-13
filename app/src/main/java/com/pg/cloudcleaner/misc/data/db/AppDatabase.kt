package com.pg.cloudcleaner.misc.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pg.cloudcleaner.misc.data.db.dao.DriveFileDao
import com.pg.cloudcleaner.misc.model.DriveFile

@Database(entities = [DriveFile::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driveFileDao(): DriveFileDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "database-name"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
