package com.pg.cloudcleaner.misc.ui.components.fileexplorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.misc.data.repo.FileActionRepo
import com.pg.cloudcleaner.misc.model.DriveFile
import com.pg.cloudcleaner.misc.ui.components.Image
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
                        AppData
                            .instance()
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

            Image(
                model = file.thumbnailLink,
                contentDescription = "",
                modifier = Modifier.size(with(LocalDensity.current) { 64.toDp() })
                // crop the image if it's not a square
            ) {
                it.error(file.iconLink?.replace("16", "64"))
            }
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
