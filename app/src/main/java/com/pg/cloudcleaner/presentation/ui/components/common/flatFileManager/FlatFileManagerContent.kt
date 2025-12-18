package com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.app.thumbnailSize
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM
import kotlin.collections.minus
import kotlin.collections.plus

@Composable
fun FlatFileManagerContent(files: List<LocalFile>, vm: SelectableDeletableVM) {

    val selectedModeOn = remember { vm.selectedModeOn }

    val selectedFiles = remember { vm.selectedFiles }

    LazyVerticalGrid(
        contentPadding = PaddingValues(vertical = 16.dp),
        columns = GridCells.Adaptive(minSize = thumbnailSize),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize()
    ) {
        items(files.size) {
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