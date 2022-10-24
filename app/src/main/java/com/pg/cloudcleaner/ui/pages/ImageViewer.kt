package com.pg.cloudcleaner.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.pg.cloudcleaner.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.model.DriveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun ImageViewer(fileID: String) {

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


    return AsyncImage(
        model = driveFile.value?.thumbnailLink,
        contentDescription = "",
        modifier = Modifier
            .fillMaxSize(),
        error = rememberAsyncImagePainter("https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg")
    )
}
