package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pg.cloudcleaner.utils.LogCompositions
import com.pg.cloudcleaner.utils.getBitmap
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun FlatFileManager(encodedDirectoryPath: String = "/storage/emulated/0") {

    LogCompositions(msg = "FlatFileManager")

    val directoryPath = URLDecoder.decode(encodedDirectoryPath, StandardCharsets.UTF_8.toString())

    val list = remember {
        mutableStateListOf<File>()
    }

    fun exploreDirectory(directoryPath: String) {
        val directory = File(directoryPath)
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory)
                exploreDirectory(file.absolutePath)
            else {
                if (!file.name.startsWith('.')) {
                    list.add(file)
                }
            }
        }
    }

    LaunchedEffect(key1 = directoryPath, block = {
        exploreDirectory(directoryPath = directoryPath)
    })

    FileListView(list = list)
}

@Composable
fun FileListView(list: List<File>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(list.size) { index ->
            FileItem(file = list[index])
        }
    }
}

@Composable
fun FileItem(file: File) {

    val bMap = remember {
        getBitmap(file)
    }
    Box {
        Column {
            Card(
                modifier = Modifier
                    .padding(4.dp)
            ) {
//                Text(list[index].name)
                AsyncImage(
                    model = bMap, contentDescription = "", contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(108.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Text(
                text = file.name,
                modifier = Modifier
                    .padding(4.dp),
                fontSize = 12.sp, overflow = TextOverflow.Ellipsis
            )
        }
    }
}
