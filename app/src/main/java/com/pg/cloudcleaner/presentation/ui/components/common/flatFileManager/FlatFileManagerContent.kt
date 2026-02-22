package com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM
import kotlin.collections.minus
import kotlin.collections.plus

@Composable
fun FlatFileManagerContent(files: List<LocalFile>,
                           columns: Int,
                           thumbnailSize: Dp,
                           vm: SelectableDeletableVM,
                           category: String = "") {
    val selectedModeOn = remember { vm.selectedModeOn }
    val selectedFiles = remember { vm.selectedFiles }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(itemSpacing),
    ) {
        items(files.size) {
            SelectableFileItem(
                file = files[it],
                thumbnailSize = thumbnailSize,
                isSelected = selectedFiles.value.contains(files[it].id),
                onCheckedChangeListener = { checked ->
                    if (checked) selectedFiles.value += files[it].id
                    else selectedFiles.value -= files[it].id
                },
                enabled = selectedModeOn.value,
                onLongClickOnItem = {
                    if (!selectedModeOn.value) {
                        selectedModeOn.value = true
                    }
                },
                category = category
            )
        }
    }
}