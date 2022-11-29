package com.pg.cloudcleaner.ui.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.misc.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.misc.model.DriveFile
import com.pg.cloudcleaner.misc.ui.components.Image
import com.pg.cloudcleaner.utils.LogCompositions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImageViewer(fileID: String) {
    LogCompositions(msg = "ImageViewer")

    val driveFile = remember {
        mutableStateOf<DriveFile?>(null)
    }

    val context: Context = LocalContext.current

    val fileRepo by lazy {
        FileActionRepoImpl(context = context)
    }

    LaunchedEffect(key1 = fileID, block = {
        withContext(Dispatchers.Default) {
            fileRepo.getFile(fileID).collect {
                driveFile.value = it
            }
        }
    })

    driveFile.value?.let { file ->
        Image(
            model = file.thumbnailLink,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    AppData
                        .instance()
                        .navController()
                        .navigate("test-page")
                },
        ) {
            it.error(file.iconLink?.replace("16", "64"))
        }
    }
}
