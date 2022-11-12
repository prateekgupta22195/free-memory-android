package com.pg.cloudcleaner.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.ui.components.Image
import com.pg.cloudcleaner.utils.getBitmapThumbnail
import com.pg.cloudcleaner.vm.FlatFileManagerViewModel
import java.io.File

@Composable
fun FlatFileManager(
    vm: FlatFileManagerViewModel = viewModel(
        factory = FlatFileManagerViewModel.Factory("/storage/emulated/0")
    )
) {
    Scaffold(topBar = { TopAppBar {} }) { padding ->
        Column(modifier = Modifier.padding(padding)) { FileListView(vm.list) }
    }
}

@Composable
fun FileListView(list: List<File>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxHeight()
    ) { items(list.size) { index -> FileItem(file = list[index]) } }
}

@Composable
fun FileItem(file: File) {

    val bMap = remember {
        getBitmapThumbnail(file)
    }
    Box {
        Column {
            Card(modifier = Modifier.padding(4.dp)) {
                Image(
                    model = file.absolutePath,
                    contentScale = ContentScale.Crop,
                    contentDescription = "",
                    modifier = Modifier
                        .height(108.dp)
                        .clip(RoundedCornerShape(4.dp))
                ) {
//                    TODO: change error image
                    it.error(R.mipmap.ic_folder)
                }
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
