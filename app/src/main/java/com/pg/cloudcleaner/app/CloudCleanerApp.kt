package com.pg.cloudcleaner.app

import AppTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pg.cloudcleaner.ui.pages.*

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CloudCleanerApp(
    modifier: Modifier = Modifier,
    startDestination: String =
        "flat-file-manager",
//            if (GoogleSignIn.getLastSignedInAccount(LocalContext.current) == null) "connect-cloud-account" else "file-explorer"
) {

    AppTheme {
        NavHost(
            modifier = modifier,
            navController = AppData.instance().navController(),
            startDestination = startDestination
        ) {

            composable("local-file-manager") {
                LocalFileManager()
            }

            composable("local-file-manager/{directory-path}") { backStackEntry ->
                LocalFileManager(directoryPath = backStackEntry.arguments!!.getString("directory-path")!!)
            }
            composable("flat-file-manager") {
                FlatFileManager()
            }

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

            composable("test-page") {
                TestPage()
            }
        }
    }
}
