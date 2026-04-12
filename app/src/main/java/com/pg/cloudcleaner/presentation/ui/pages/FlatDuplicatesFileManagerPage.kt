package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import android.text.format.Formatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.vm.FlatDuplicatesFileManagerVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlatFileManager(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val list by vm.duplicateFiles.collectAsState()

    // Check if all groups are selected for button text
    val allGroupsSelected = list?.values?.all { group ->
        if (group.size <= 1) return@all true
        val filesExceptFirst = group.drop(1)
        filesExceptFirst.all { file -> vm.selectedFileIds.value.contains(file.id) }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.category_duplicate_media)) },
            navigationIcon = { BackNavigationIconCompose() },
            actions = {
                if (!list.isNullOrEmpty()) {
                    TextButton(
                        onClick = { vm.toggleAllGroups() }
                    ) {
                        Text(if (allGroupsSelected == true) stringResource(R.string.action_deselect_all) else stringResource(R.string.action_select_all))
                    }
                }
            }
        )
    }, floatingActionButton = {
        DeleteButton()
    }) { padding ->
        Box(modifier = Modifier.padding(padding)) { FileListView() }
    }
}


@Composable
fun DeleteButton(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val context = LocalContext.current
    val selectedFileIds = remember { vm.selectedFileIds }
    val showDeleteDialog = remember { vm.showDeleteDialog }
    val isDeleting = remember { vm.isDeleting }
    val allFiles = vm.duplicateFiles.collectAsState().value?.values?.flatten() ?: emptyList()
    val selectedSize = allFiles.filter { it.id in selectedFileIds.value }.sumOf { it.size } * 1024
    val formattedSize = Formatter.formatFileSize(context, selectedSize)

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { vm.deleteFiles(selectedFileIds.value) },
            enabled = selectedFileIds.value.isNotEmpty() && !isDeleting.value,
            modifier = Modifier.align(Alignment.Center),
        ) {
            if (selectedFileIds.value.isEmpty()) {
                Text(stringResource(R.string.action_delete))
            } else {
                Text("Delete ${selectedFileIds.value.size} files · $formattedSize")
            }
        }
    }

    if (showDeleteDialog.value || isDeleting.value) {
        PopupCompose(show = true, onPopupDismissed = { if (!isDeleting.value) vm.cancelDelete() }) {
            AlertDialog(
                onDismissRequest = { if (!isDeleting.value) vm.cancelDelete() },
                title = { Text(if (isDeleting.value) stringResource(R.string.deleting_files_title) else stringResource(R.string.delete_files_title)) },
                text = {
                    if (isDeleting.value) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.deleting_files_progress, selectedFileIds.value.size))
                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Text(stringResource(R.string.delete_files_confirmation, selectedFileIds.value.size, formattedSize))
                    }
                },
                confirmButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = { vm.confirmDeleteFiles() }) {
                            Text(stringResource(R.string.action_delete), color = androidx.compose.ui.graphics.Color.Red)
                        }
                    }
                },
                dismissButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = { vm.cancelDelete() }) { Text(stringResource(R.string.action_cancel)) }
                    }
                }
            )
        }
    }
}

@Composable
fun FileListView(vm: FlatDuplicatesFileManagerVM = viewModel()) {
    val list = vm.duplicateFiles.collectAsState()

    LaunchedEffect(key1 = Unit) {
        vm.selectDuplicateFiles()
    }

    if (list.value == null) {
        DuplicatesShimmer()
        return
    }

    LazyColumn {
        items(list.value!!.keys.size) {
            val key = list.value!!.keys.toList()[it]
            key(key) {
                HorizontalDuplicateFiles(list.value!![key]!!)
            }
        }
    }
}

@Composable
private fun DuplicatesShimmer() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmerAlpha",
    )
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(6) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                // Header row placeholder
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Thumbnail row placeholder (2-3 items)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                ) {
                    items(if (it % 2 == 0) 3 else 2) {
                        Box(
                            modifier = Modifier
                                .size(thumbnailSize)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.duplicates_count_label, data.size - 1))
            if (data.size > 1) {
                val buttonText = if (vm.areAllExceptFirstSelected(data)) {
                    stringResource(R.string.action_deselect_all)
                } else {
                    stringResource(R.string.action_select_all)
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
                    enabled = true,
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
                    category = "category_duplicates"
                )
            }
        }
    }
}
