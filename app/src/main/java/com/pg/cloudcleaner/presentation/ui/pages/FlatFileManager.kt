package com.pg.cloudcleaner.presentation.ui.pages

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.misc.ui.components.Image
import com.pg.cloudcleaner.presentation.vm.FlatFileManagerViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


val paddingModifier = Modifier.padding(4.dp)

val imageModifier = Modifier
    .height(108.dp)
    .clip(RoundedCornerShape(4.dp))

@Composable
fun FlatFileManager() {
    Scaffold(topBar = { TopAppBar {} }) { padding ->
        Column(modifier = Modifier.padding(padding)) { FileListView() }
    }
    Timber.d(Environment.getExternalStorageDirectory().absolutePath)
}

@Composable
fun FileListView(
    vm: FlatFileManagerViewModel = viewModel()

) {
    val scope = rememberCoroutineScope()
    val list = vm.readFiles().collectAsState(initial = emptyList())
    LaunchedEffect(key1 = Unit, block = {
        scope.launch(Dispatchers.IO + CoroutineExceptionHandler { a, b ->
            Timber.d("ABc hello")
            vm.readFiles()
        }) {
            Timber.d("hole" + Thread.currentThread().name)
        }
    })

    val lazyState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxHeight(),
        state = lazyState
    ) {

        itemsIndexed(list.value) { _, file ->
            key(file.id) {
                FileItem(file = file)
            }
        }
    }
}


@Composable
fun FileItem(
    file: LocalFile,
    vm: FlatFileManagerViewModel = viewModel()
) {

    Box(modifier = Modifier.clickable {
//        Uri.fromFile(file).open(context)
        vm.deleteFile(localFile = file)
    }) {
        Column {
            Card(modifier = paddingModifier) {
                Image(
                    model = file.id,
                    contentScale = ContentScale.Crop,
                    contentDescription = "",
                    modifier = imageModifier
                ) {
//                    TODO: change error image
                    it.error(R.mipmap.ic_folder)

                }
            }
            if (file.fileName != null)
                Text(
                    text = file.fileName,
                    modifier = paddingModifier,
                    fontSize = 12.sp, overflow = TextOverflow.Ellipsis
                )
        }
    }
}
