package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
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
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.vm.FlatDuplicatesFileManagerVM
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatFileManager(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val scope = rememberCoroutineScope()
    val list = vm.readFiles().collectAsState(initial = emptyMap())
    
    // Check if all groups are selected for button text
    val allGroupsSelected = list.value.values.all { group ->
        if (group.size <= 1) return@all true
        val filesExceptFirst = group.drop(1)
        filesExceptFirst.all { file -> vm.selectedFileIds.value.contains(file.id) }
    }
    
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Duplicate Files") },
            navigationIcon = { BackNavigationIconCompose() },
            actions = {
                if (list.value.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                vm.toggleAllGroups()
                            }
                        }
                    ) {
                        Text(if (allGroupsSelected) "Deselect All" else "Select All")
                    }
                }
            }
        )
    }, bottomBar = {
        DeleteButton()
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) { FileListView() }
    }
}


@Composable
fun DeleteButton(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val selectedFileIds = remember { vm.selectedFileIds }
    val showDeleteDialog = remember { vm.showDeleteDialog }

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.navigationBars)
            .padding(16.dp)
    ) {

        Button(
            onClick = {
                scope.launch {
                    vm.deleteFiles(selectedFileIds.value)
                }
            },
            enabled = selectedFileIds.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = if (selectedFileIds.value.isEmpty()) "Delete" else "Delete ${selectedFileIds.value.size} Files")
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        PopupCompose(show = true, onPopupDismissed = { vm.cancelDelete() }) {
            AlertDialog(
                onDismissRequest = { vm.cancelDelete() },
                title = { Text("Delete Files") },
                text = { 
                    Text("Are you sure you want to delete ${selectedFileIds.value.size} files? You will not be able to recover them.") 
                },
                confirmButton = {
                    TextButton(onClick = { vm.confirmDeleteFiles() }) { 
                        Text("Delete", color = androidx.compose.ui.graphics.Color.Red) 
                    }
                },
                dismissButton = { TextButton(onClick = { vm.cancelDelete() }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun FileListView(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val scope = rememberCoroutineScope()
    val list = vm.readFiles().collectAsState(initial = emptyMap())

    LaunchedEffect(key1 = Unit, block = {
        scope.launch(Dispatchers.IO + CoroutineExceptionHandler { a, b ->
            Timber.d("ABc hello")
        }) {
            vm.selectDuplicateFiles()
            Timber.d("hole" + Thread.currentThread().name)
        }
    })

    LazyColumn {
        items(list.value.keys.size) {
            val key = list.value.keys.toList()[it]
            key(key) {
                HorizontalDuplicateFiles(list.value[key]!!)
            }
        }

    }
}


@Composable
fun HorizontalDuplicateFiles(
    data: List<LocalFile>, vm: FlatDuplicatesFileManagerVM = viewModel()
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${data.size - 1} Duplicates")
            if (data.size > 1) {
                val buttonText = if (vm.areAllExceptFirstSelected(data)) {
                    "Deselect All"
                } else {
                    "Select All"
                }
                TextButton(
                    onClick = { vm.toggleGroupSelection(data) }
                ) {
                    Text(buttonText)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(data.size) {
                val selectedFileIds = remember {
                    vm.selectedFileIds
                }
                SelectableFileItem(
                    data[it], thumbnailSize = thumbnailSize,
                    enabled = true, // Explicitly enable checkbox
                    isSelected = selectedFileIds.value.contains(data[it].id),
                    onCheckedChangeListener = { checked ->
                        if (checked) {
                            vm.uncheckedFiles.remove(data[it].id)
                            selectedFileIds.value += data[it].id
                        } else {
                            vm.uncheckedFiles.add(data[it].id)
                            selectedFileIds.value -= data[it].id
                        }
                    },
                    category = "category_duplicates")
            }
        }
    }
}



