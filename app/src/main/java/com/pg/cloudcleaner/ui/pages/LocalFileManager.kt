package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.utils.LogCompositions
import com.pg.cloudcleaner.utils.getMimeType
import timber.log.Timber
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LocalFileManager(directoryPath: String = "/storage/emulated/0") {

    LogCompositions(msg = "LocalFileManager")

    val directory = File(URLDecoder.decode(directoryPath, StandardCharsets.UTF_8.toString()))
    directory.listFiles()?.toList()?.let { files ->
        Files(files = files)
    }
}

@Composable
fun Files(files: List<File>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(files.size) { index ->
            Item(file = files[index])
        }
    }
}

@Composable
fun Item(file: File) {

    Timber.d("mime type ${getMimeType(file.absolutePath)}")
    @Composable
    fun FileItem(file: File) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Icon(Icons.Filled.Folder, "folder icon")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(file.name)
                }
                Text("${file.length() / 1024} KB")
            }
        }
    }

    @Composable
    fun DirectoryItem(file: File) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    AppData
                        .instance()
                        .navController()
                        .navigate("local-file-manager/${URLEncoder.encode(file.absolutePath, StandardCharsets.UTF_8.toString())}")
                }
        ) {
            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Icon(Icons.Filled.Folder, "folder icon")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(file.name)
                }
            }
        }
    }

    if (file.isDirectory) DirectoryItem(file) else FileItem(file)
}
