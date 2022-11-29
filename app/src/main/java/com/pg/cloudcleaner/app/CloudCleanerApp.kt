package com.pg.cloudcleaner.app

import AppTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pg.cloudcleaner.ui.pages.FlatFileManager

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CloudCleanerApp(
    modifier: Modifier = Modifier,
    startDestination: String =
        "flat-file-manager",
) {

    AppTheme {
        NavHost(
            modifier = modifier,
            navController = AppData.instance().navController(),
            startDestination = startDestination
        ) {

            composable("flat-file-manager") {
                FlatFileManager()
            }
        }
    }
}
