package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.data.repo.FileActionRepo
import com.pg.cloudcleaner.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.model.DriveFile
import kotlinx.coroutines.*

@ExperimentalFoundationApi
@Composable
fun FileExplorer() {

    val filesMutable = remember { mutableStateOf<List<DriveFile>?>(null) }

    val context = LocalContext.current

    val fileRepo = remember {
        FileActionRepoImpl(context = context)
    }

    if (filesMutable.value != null)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {

            items(filesMutable.value!!.size) { index ->
                DriveFileItem(filesMutable.value!![index], fileRepo)
            }
        }

    LaunchedEffect(key1 = filesMutable.value, block = {

        withContext(Dispatchers.IO) {
            fileRepo.getAllFiles().collect {
                filesMutable.value = it
            }
        }
    })
}

@ExperimentalFoundationApi
@Composable
fun DriveFileItem(file: DriveFile, fileActionRepo: FileActionRepo) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.combinedClickable(
            onClick = {
                if (file.fileType?.contains("image") == true) {
                    AppData.instance().navController.navigate("image-viewer/${file.id}")
                }
            }, onDoubleClick = {
            CoroutineScope(
                Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
                    throwable.printStackTrace()
                }
            ).launch {
                fileActionRepo.deleteFile(file)
            }
        }

        )
    ) {

        AsyncImage(
            model = file.thumbnailLink,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .fillMaxSize()
                .clip(
                    CircleShape
                ),
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter("https://thumbs.dreamstime.com/b/beautiful-rain-forest-ang-ka-nature-trail-doi-inthanon-national-park-thailand-36703721.jpg")

            // crop the image if it's not a square
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            if (file.fileName != null)
                Text(file.fileName)

            Row {
                if (file.lastViewedTime != null)
                    Row {
                        Text(file.lastViewedTime, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(width = 16.dp))
                    }

                if (file.fileType != null)
                    Text(file.fileType, fontSize = 12.sp)
            }
        }
    }
}
