package com.pg.cloudcleaner.app

import AppTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost

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


