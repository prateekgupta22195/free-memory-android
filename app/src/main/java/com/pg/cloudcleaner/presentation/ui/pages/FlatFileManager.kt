package com.pg.cloudcleaner.presentation.ui.pages

import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.FileItem
import com.pg.cloudcleaner.presentation.vm.FlatFileManagerViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


val paddingModifier = Modifier.padding(4.dp)

val imageModifier = Modifier
    .height(108.dp)
    .width(108.dp)
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
    val list = vm.readFiles().collectAsState(initial = emptyMap())
    LaunchedEffect(key1 = Unit, block = {
        scope.launch(Dispatchers.IO + CoroutineExceptionHandler { a, b ->
            Timber.d("ABc hello")
            vm.readFiles()
        }) {
            Timber.d("hole" + Thread.currentThread().name)
        }
    })

    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,

        ) {


        items(list.value.keys.size) {
            val key = list.value.keys.toList()[it]
            key(key) {
                HorizontalDuplicateImages(list.value[key]!!)
            }
        }

    }
}


@Composable
fun HorizontalDuplicateImages(data: List<LocalFile>, vm: FlatFileManagerViewModel = viewModel()) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(data.size) {
            key(data[it].id) {
                FileItem(data[it], onClick = {
//                    vm.deleteFile(data[it])
                    val navController = App.instance.navController()
                    navController.currentBackStackEntry?.arguments?.putString(
                        "filePath", data[it].id
                    )
                    navController.navigate(Routes.DOCUMENT_VIEWER)
                })
            }
        }
    }
}



