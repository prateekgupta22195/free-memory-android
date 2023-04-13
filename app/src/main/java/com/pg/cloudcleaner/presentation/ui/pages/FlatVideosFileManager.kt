package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.Deleteable
import com.pg.cloudcleaner.presentation.ui.components.FNTextButton
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.FlatVideosFileManagerVM

@Composable
fun FlatVideosFileManager(vm: FlatVideosFileManagerVM = viewModel()) {

    val selectedFiles = remember {
        mutableListOf<String>()
    }


    val files = vm.getVideoFiles().collectAsState(initial = listOf())





    Deleteable(content = { VideosContent(files.value) }, deleteButton = {

    }, pageTitle = "Video Files", actions = {


        FNTextButton(onClick = {

        }, text = "Select")
//        if(selectedFiles.isEmpty()) {
//

//        } else {
//            FNTextButton(onClick = {
//
//            }, text = "Cancel")
//        }

    })


}

@Composable
fun VideosContent(files: List<LocalFile>) {
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 100.dp)) {
        items(files.size) {
            SelectableFileItem(file = files[it])
        }
    }
}