package com.pg.cloudcleaner.misc.ui.components.fileexplorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.misc.data.repo.FileActionRepo
import com.pg.cloudcleaner.misc.model.DriveFile
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun DriveFileItem(file: DriveFile, fileActionRepo: FileActionRepo) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (file.fileType?.contains("image") == true) {
                        App
                            .instance
                            .navController()
                            .navigate("image-viewer/${file.id}")
                    }
                }, onDoubleClick = {
                    scope.launch(
                        Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
                            throwable.printStackTrace()
                        }
                    ) {
                        fileActionRepo.deleteFile(file)
                    }
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
        ) {

            AsyncImage(
                file.thumbnailLink,
                contentDescription = "",
                modifier = Modifier.size(with(LocalDensity.current) { 64.toDp() })
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
}
