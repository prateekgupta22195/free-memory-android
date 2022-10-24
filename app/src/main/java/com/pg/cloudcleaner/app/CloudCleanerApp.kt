package com.pg.cloudcleaner.app

import AppTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.pg.cloudcleaner.ui.pages.ConnectCloudAccount
import com.pg.cloudcleaner.ui.pages.FileExplorer
import com.pg.cloudcleaner.ui.pages.ImageViewer

@ExperimentalFoundationApi
@Composable
fun CloudCleanerApp(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = if (GoogleSignIn.getLastSignedInAccount(LocalContext.current) == null) "connect-cloud-account" else "file-explorer",
) {

    AppTheme {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {

            composable("connect-cloud-account") {
                ConnectCloudAccount()
            }

            composable("file-explorer") {
                FileExplorer()
            }

            composable("image-viewer/{file-id}") { backStackEntry ->
                val imageId = backStackEntry.arguments!!.getString("file-id")!!
                ImageViewer(imageId)
            }
        }
    }
}
