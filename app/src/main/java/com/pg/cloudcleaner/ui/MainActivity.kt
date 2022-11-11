package com.pg.cloudcleaner.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.compose.rememberNavController
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.app.CloudCleanerApp

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppData.instance().initNavController(rememberNavController())
            CloudCleanerApp()
        }
    }
}
