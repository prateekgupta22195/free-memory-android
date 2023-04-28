package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.FlatImagesFileManagerVM
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatImagesFileManager(vm: FlatImagesFileManagerVM = viewModel()) {
    val selectedModeOn = remember {
        vm.selectedModeOn
    }


    val files = vm.getImageFiles().collectAsState(initial = listOf())



    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Images")
        }, actions = {

            if (!selectedModeOn.value) TextButton(onClick = {
                selectedModeOn.value = true
            }) {
                Text("Select")
            }
            else TextButton(onClick = {
                selectedModeOn.value = false
            }) {
                Text("Cancel")
            }
        }, navigationIcon = { BackNavigationIconCompose() })
    }, bottomBar = {
        if (selectedModeOn.value) DeleteButtonComposable1()
    }) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ImagesContent(files.value)
        }
    }

}

@Composable
fun ImagesContent(files: List<LocalFile>, vm: FlatImagesFileManagerVM = viewModel()) {


    val selectedModeOn = remember {
        vm.selectedModeOn
    }

    val selectedFiles = remember {
        vm.selectedFiles
    }



    LazyVerticalGrid(
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 12.dp),
        columns = GridCells.Adaptive(minSize = thumbnailSize + 8.dp),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize()
    ) {
        items(files.size) {
            Box(
                modifier = Modifier
                    .size(thumbnailSize)
                    .padding(horizontal = 4.dp)
            ) {
                SelectableFileItem(
                    file = files[it],
                    isSelected = selectedFiles.value.contains(files[it].id),
                    onCheckedChangeListener = { checked ->
                        if (checked) selectedFiles.value += files[it].id
                        else selectedFiles.value -= files[it].id
                    },
                    enabled = selectedModeOn.value
                )
            }
        }
    }
}


@Composable
fun DeleteButtonComposable1(vm: FlatImagesFileManagerVM = viewModel()) {
    val selectedFileIds = remember { vm.selectedFiles }
    val selectedModeOn = remember {
        vm.selectedModeOn
    }

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    vm.deleteFiles(selectedFileIds.value)
                }
                selectedModeOn.value = false
            },
            enabled = selectedFileIds.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = if (selectedFileIds.value.isEmpty()) "Delete" else "Delete ${selectedFileIds.value.size} Files")
        }
    }

}