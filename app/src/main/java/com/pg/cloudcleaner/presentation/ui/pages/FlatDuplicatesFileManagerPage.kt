package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.FlatDuplicatesFileManagerVM
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatFileManager() {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Duplicate Files") },
            navigationIcon = { BackNavigationIconCompose() },
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
            },
            enabled = selectedFileIds.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = if (selectedFileIds.value.isEmpty()) "Delete" else "Delete ${selectedFileIds.value.size} Files")
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
        Text("${data.size - 1} Duplicates", modifier = Modifier.padding(start = 16.dp))
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
                SelectableFileItem(data[it],
                    isSelected = selectedFileIds.value.contains(data[it].id),
                    onCheckedChangeListener = { checked ->
                        if (checked) {
                            vm.uncheckedFiles.remove(data[it].id)
                            selectedFileIds.value += data[it].id
                        } else {
                            vm.uncheckedFiles.add(data[it].id)
                            selectedFileIds.value -= data[it].id
                        }
                    })
            }
        }
    }
}



