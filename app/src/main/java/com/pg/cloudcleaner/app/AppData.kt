package com.pg.cloudcleaner.app

import androidx.navigation.NavHostController
import com.pg.cloudcleaner.BuildConfig
import timber.log.Timber

class AppData {

    private lateinit var navController: NavHostController

    fun navController(): NavHostController {
        return instance().navController
    }

    fun initNavController(navController: NavHostController) {
        this.navController = navController
    }

    companion object {

        @Volatile
        private var INSTANCE: AppData? = null

        fun instance(): AppData {

            if (INSTANCE != null)
                return INSTANCE!!

            synchronized(this) {
                initLibraries()
                INSTANCE = AppData()
                return INSTANCE!!
            }
        }

        private fun initLibraries() {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        }
    }
}
