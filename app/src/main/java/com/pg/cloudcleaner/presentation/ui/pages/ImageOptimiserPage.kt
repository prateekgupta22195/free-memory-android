package com.pg.cloudcleaner.presentation.ui.pages

import android.content.res.Configuration
import android.text.format.Formatter
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.cloudcleaner.app.itemSpacing
import com.pg.cloudcleaner.presentation.ui.components.BackNavigationIconCompose
import com.pg.cloudcleaner.presentation.ui.components.SelectableFileItem
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.vm.ImageOptimiserVM
import com.pg.cloudcleaner.utils.ImageOptimizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageOptimiserPage(vm: ImageOptimiserVM = viewModel()) {
    val context = LocalContext.current
    val images by vm.images.collectAsState()
    val selectedIds = remember { vm.selectedFileIds }
    val isOptimising = remember { vm.isOptimising }
    val showDialog = remember { vm.showConfirmDialog }

    LaunchedEffect(images) {
        images?.let { vm.initSelection(it) }
    }

    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 6
    val thumbnailSize = configuration.screenWidthDp.dp / columns

    val selectedSizeBytes = (images?.filter { it.id in selectedIds.value }?.sumOf { it.size } ?: 0L) * 1024L
    val estimatedSavings = ImageOptimizer.estimatedSavings(selectedSizeBytes)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Optimise Images") },
                navigationIcon = { BackNavigationIconCompose() },
                actions = {
                    if (!images.isNullOrEmpty()) {
                        val allSelected = images!!.all { it.id in selectedIds.value }
                        TextButton(onClick = { vm.toggleAll(images!!) }) {
                            Text(if (allSelected) "Deselect All" else "Select All")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { vm.requestOptimise() },
                    enabled = selectedIds.value.isNotEmpty() && !isOptimising.value,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    if (selectedIds.value.isEmpty()) {
                        Text("Optimise")
                    } else {
                        Text(
                            "Optimise ${selectedIds.value.size} ${if (selectedIds.value.size == 1) "image" else "images"}" +
                                    " · ~${Formatter.formatFileSize(context, estimatedSavings)} savings"
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                images == null -> ImageOptimiserShimmer(columns, thumbnailSize)
                images!!.isEmpty() -> EmptyOptimiserState()
                else -> {
                    val currentImages = images!!
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(itemSpacing),
                    ) {
                        items(currentImages.size, key = { currentImages[it].id }) { index ->
                            val file = currentImages[index]
                            SelectableFileItem(
                                file = file,
                                thumbnailSize = thumbnailSize,
                                isSelected = file.id in selectedIds.value,
                                enabled = true,
                                onCheckedChangeListener = { _ -> vm.toggleSelection(file.id) },
                                category = "category_images",
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog.value || isOptimising.value) {
        PopupCompose(show = true, onPopupDismissed = { if (!isOptimising.value) vm.cancelOptimise() }) {
            AlertDialog(
                onDismissRequest = { if (!isOptimising.value) vm.cancelOptimise() },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(if (isOptimising.value) "Optimising…" else "Optimise Images")
                },
                text = {
                    if (isOptimising.value) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Text(
                                "Optimised ${vm.optimisedCount.intValue} of ${vm.totalToOptimise.intValue} images…",
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "This will compress ${selectedIds.value.size} JPEG " +
                                        "${if (selectedIds.value.size == 1) "image" else "images"} " +
                                        "at ${ImageOptimizer.QUALITY}% quality, saving approximately " +
                                        "${Formatter.formatFileSize(context, estimatedSavings)}."
                            )
                            Text(
                                "This action is not reversible. The original images will be permanently replaced.",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                },
                confirmButton = {
                    if (!isOptimising.value) {
                        TextButton(onClick = { vm.confirmOptimise() }) {
                            Text("Optimise", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                dismissButton = {
                    if (!isOptimising.value) {
                        TextButton(onClick = { vm.cancelOptimise() }) { Text("Cancel") }
                    }
                },
            )
        }
    }
}

@Composable
private fun EmptyOptimiserState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                "All images are optimised",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "No JPEG images larger than 500 KB were found.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ImageOptimiserShimmer(columns: Int, thumbnailSize: Dp) {
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
