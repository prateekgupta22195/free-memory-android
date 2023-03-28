package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.FileItem
import com.pg.cloudcleaner.presentation.vm.HomeViewModel
import kotlinx.coroutines.launch


@Composable
fun DataCategories() {

    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,

        ) {


        items(0) {
            val key = "duplicate_images"
            key(key) {
//                HorizontalDuplicateImages(list.value[key]!!)
            }
        }

    }
}


@Composable
fun CategoryDuplicate(vm: HomeViewModel = viewModel()) {

    val list = vm.getAnyTwoDuplicateImage().collectAsState(initial = null)
    if (list.value != null) {
        Column {
            Text(text = "Duplicate Images")
            Row(modifier = Modifier.fillMaxWidth()) {
                FileItem(id = list.value!!.first.id, onClick = {

                })
                FileItem(id = list.value!!.second.id, onClick = {

                })
            }
        }
    }

}

