package com.pg.cloudcleaner.app

import android.app.Application
import androidx.navigation.NavHostController
import androidx.room.Room
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import com.pg.cloudcleaner.BuildConfig
import com.pg.cloudcleaner.data.db.AppDatabase
import timber.log.Timber


class App : Application() {

    private lateinit var navController: NavHostController

    lateinit var imageLoader: ImageLoader

    lateinit var db: AppDatabase


    fun navController(): NavHostController {
        return instance.navController
    }

    fun initNavController(navController: NavHostController) {
        this.navController = navController
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initDB()
        initLibraries()
    }

    private fun initDB() {
        db = Room.databaseBuilder(
            instance.applicationContext, AppDatabase::class.java, "database-name"
        ).build()
    }

    private fun initLibraries() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


        imageLoader =
            ImageLoader.Builder(instance).diskCachePolicy(CachePolicy.ENABLED).components {
                add(VideoFrameDecoder.Factory())
            }.crossfade(true).build()

    }

    companion object {
        lateinit var instance: App
            private set
    }
}
