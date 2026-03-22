package com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM

@Composable
fun FlatFileManagerContent(
    files: List<LocalFile>?,
    columns: Int,
    thumbnailSize: Dp,
    vm: SelectableDeletableVM,
    category: String = "",
) {
    if (files == null) {
        FlatFileManagerShimmer(columns, thumbnailSize)
        return
    }

    val selectedModeOn = remember { vm.selectedModeOn }
    val selectedFiles = remember { vm.selectedFiles }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(itemSpacing),
    ) {
        items(files.size, key = { files[it].id }) {
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

@Composable
private fun FlatFileManagerShimmer(columns: Int, thumbnailSize: Dp) {
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(itemSpacing),
    ) {
        items(30) {
            Box(
                modifier = Modifier
                    .size(thumbnailSize)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
            )
        }
    }
}