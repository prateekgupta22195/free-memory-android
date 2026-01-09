package com.pg.cloudcleaner.app

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.room.Room
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.video.VideoFrameDecoder
import com.pg.cloudcleaner.BuildConfig
import com.pg.cloudcleaner.app.uim3.theme.AppTheme
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

        imageLoader = ImageLoader.Builder(instance).memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED).components {
                add(VideoFrameDecoder.Factory())
            }.crossfade(true).build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}


@ExperimentalFoundationApi
@Composable
fun CloudCleanerApp(
    modifier: Modifier = Modifier,
    startDestination: String = Routes.HOME,
) {
    AppTheme {
        NavHost(
            modifier = modifier,
            navController = App.instance.navController(),
            startDestination = startDestination,
            builder = router
        )
    }
}
