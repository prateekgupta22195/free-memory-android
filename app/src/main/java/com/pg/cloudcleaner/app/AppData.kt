package com.pg.cloudcleaner.app

import androidx.navigation.NavHostController

class AppData {

    lateinit var navController: NavHostController


    companion object {

        @Volatile
        private var INSTANCE : AppData? = null

        fun instance() : AppData {

            if(INSTANCE!=null)
                return INSTANCE!!

            synchronized(this) {
                INSTANCE = AppData()
                return INSTANCE!!
            }

        }
    }


}