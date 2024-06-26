package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.pg.cloudcleaner.app.App

@Composable
fun BackNavigationIconCompose() {
    val navController = App.instance.navController()
    if (navController.previousBackStackEntry != null) {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack, contentDescription = "Back"
            )
        }
    }
}