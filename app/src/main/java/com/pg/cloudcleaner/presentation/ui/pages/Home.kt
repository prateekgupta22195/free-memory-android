package com.pg.cloudcleaner.presentation.ui.pages

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.presentation.ui.components.FileItem
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.HomeViewModel


@Composable
@Preview
fun DataCategories() {

    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {


        item {
            key("duplicate_files") {
                CategoryDuplicateFiles()
            }
        }

        item {
            key("images") {
                CategoryImages()
            }
        }


        item {
            key("videos") {
                CategoryVideos()
            }
        }

    }

}


@Composable
fun CategoryDuplicateFiles(vm: HomeViewModel = viewModel()) {
    val list = vm.getAnyTwoDuplicateFiles().collectAsState(initial = null)
    val scrollableState = rememberScrollState()
    if (list.value != null) {
        Card(elevation = 8.dp, modifier = Modifier
            .fillMaxSize()
            .clickable {

            }) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(text = "Duplicate Files", modifier = Modifier.padding(bottom = 16.dp))
                Row(
                    modifier = Modifier.horizontalScroll(state = scrollableState)
                ) {
                    FileItem(list.value!!.first, onClick = {

                        val navController = App.instance.navController()
                        val fileUrl = Uri.encode(list.value!!.first.id)
                        navController.navigate(Routes.DOCUMENT_VIEWER + "?url=$fileUrl")
                    })
                    FileItem(list.value!!.second, onClick = {
                        val navController = App.instance.navController()
                        navController.navigate(Routes.DOCUMENT_VIEWER + "/${list.value!!.second.id}")
                    })
                }
            }
        }
    }

}

@Composable
fun CategoryImages(vm: HomeViewModel = viewModel()) {
    val videoFile = vm.getImageFiles().collectAsState(initial = null)
    if (!videoFile.value.isNullOrEmpty()) {
        Card(elevation = 8.dp, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Image Files", modifier = Modifier.padding(bottom = 16.dp))
                LazyRow {
                    items(videoFile.value!!.size) {
                        val file = videoFile.value!![it]
                        key(file) {
                            FileItem(videoFile.value!![it],
                                onClick = {
                                    val navController = App.instance.navController()
                                    val fileUrl = Uri.encode(videoFile.value!![it].id)
                                    navController.navigate(Routes.PDF_VIEWER + "?url=$fileUrl")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryVideos(vm: HomeViewModel = viewModel()) {
    val videoFile = vm.getVideoFiles().collectAsState(initial = null)
    if (!videoFile.value.isNullOrEmpty()) {
        Card(elevation = 8.dp, modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Video Files", modifier = Modifier.padding(bottom = 16.dp))
                LazyRow {
                    items(videoFile.value!!.size) {
                        val file = videoFile.value!![it]
                        key(file) {
                            FileItem(videoFile.value!![it], onClick = {
                                val navController = App.instance.navController()
                                val fileUrl = Uri.encode(videoFile.value!![it].id)
                                navController.navigate(Routes.PDF_VIEWER + "?url=$fileUrl")
                            })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AllFiles() {

}

