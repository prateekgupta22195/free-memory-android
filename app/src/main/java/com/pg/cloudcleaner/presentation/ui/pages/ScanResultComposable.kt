package com.pg.cloudcleaner.presentation.ui.pages

import android.text.format.Formatter
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import coil3.compose.AsyncImage
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.Routes
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.presentation.vm.HomeVM

@Composable
fun ScanResultComposable(vm: HomeVM = viewModel()) {
    val context = LocalContext.current

    val totalFreeableBytes by vm.totalFreeableBytes.collectAsState()
    val duplicateThumbnails by vm.duplicateThumbnails.collectAsState()
    val imageFiles by vm.previewImageFiles.collectAsState()
    val videoFiles by vm.previewVideoFiles.collectAsState()
    val largeFiles by vm.previewLargeFiles.collectAsState()

    val duplicatesCount by vm.duplicatesCount.collectAsState()
    val imagesCount by vm.imagesCount.collectAsState()
    val videosCount by vm.videosCount.collectAsState()
    val largeFilesCount by vm.largeFilesCount.collectAsState()

    val duplicateSizeBytes by vm.duplicateSizeBytes.collectAsState()
    val imageSizeBytes by vm.imageSizeBytes.collectAsState()
    val videoSizeBytes by vm.videoSizeBytes.collectAsState()
    val largeSizeBytes by vm.largeSizeBytes.collectAsState()
    val optimizableCount by vm.optimizableImagesCount.collectAsState()
    val optimizableSizeBytes by vm.optimizableImagesSizeBytes.collectAsState()
    val optimizablePreview by vm.previewOptimizableImages.collectAsState()
    val screenshotsCount by vm.screenshotsCount.collectAsState()
    val screenshotsSizeBytes by vm.screenshotsSizeBytes.collectAsState()
    val screenshotsThumbnails by vm.previewScreenshots.collectAsState()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var categoriesYOffset by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(scrollState)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Scan Complete",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Found files you can delete to free up space",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Summary Card ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurface,
                                MaterialTheme.colorScheme.primary,
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(
                                Float.POSITIVE_INFINITY,
                                Float.POSITIVE_INFINITY
                            ),
                        )
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Total space you can free",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                val animatedBytes by animateFloatAsState(
                    targetValue = totalFreeableBytes.toFloat(),
                    animationSpec = tween(durationMillis = 1500),
                    label = "totalFreeableSpace",
                )
                Text(
                    text = Formatter.formatFileSize(context, animatedBytes.toLong()),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollTo(categoriesYOffset)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = "Start cleaning",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Categories ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .onGloballyPositioned { categoriesYOffset = it.positionInParent().y.toInt() },
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Duplicate Photos
            ScanResultCategoryCard(
                title = "Duplicate Media",
                subtitle = "$duplicatesCount files • ${Formatter.formatFileSize(context, duplicateSizeBytes)}",
                accentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Outlined.ContentCopy,
                thumbnails = duplicateThumbnails,
                onReviewClick = {
                    App.instance.navController().navigate(Routes.FLAT_DUPLICATES_FILE_MANAGER)
                },
            )

            // Old Screenshots / Images
            ScanResultCategoryCard(
                title = "Large Images",
                subtitle = "$imagesCount files • ${Formatter.formatFileSize(context, imageSizeBytes)}",
                accentColor = MaterialTheme.colorScheme.tertiary,
                icon = Icons.Outlined.Image,
                thumbnails = imageFiles,
                onReviewClick = {
                    App.instance.navController().navigate(Routes.FLAT_IMAGES_FILE_MANAGER)
                },
            )

            // Large Videos
            ScanResultCategoryCard(
                title = "Large Videos",
                subtitle = "$videosCount files • ${Formatter.formatFileSize(context, videoSizeBytes)}",
                accentColor = MaterialTheme.colorScheme.secondary,
                icon = Icons.Outlined.VideoFile,
                thumbnails = videoFiles,
                onReviewClick = {
                    App.instance.navController().navigate(Routes.FLAT_VIDEOS_FILE_MANAGER)
                },
            )

            // Screenshots
            if (screenshotsCount > 0) {
                ScanResultCategoryCard(
                    title = "Screenshots",
                    subtitle = "$screenshotsCount files • ${Formatter.formatFileSize(context, screenshotsSizeBytes)}",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Outlined.Screenshot,
                    thumbnails = screenshotsThumbnails,
                    onReviewClick = {
                        App.instance.navController().navigate(Routes.FLAT_SCREENSHOTS_FILE_MANAGER)
                    },
                )
            }

            // Large Files
            ScanResultCategoryCard(
                title = "Large Files",
                subtitle = "$largeFilesCount files • ${Formatter.formatFileSize(context, largeSizeBytes)}",
                accentColor = MaterialTheme.colorScheme.onErrorContainer,
                icon = Icons.Outlined.FolderOpen,
                thumbnails = largeFiles,
                onReviewClick = {
                    App.instance.navController().navigate(Routes.FLAT_LARGE_FILE_MANAGER)
                },
            )

            // Image Optimiser
            if (optimizableCount > 0) {
                val estimatedSavings = (optimizableSizeBytes * 0.5).toLong()
                ScanResultCategoryCard(
                    title = "Optimise Images",
                    subtitle = "$optimizableCount JPEG ${if (optimizableCount == 1) "image" else "images"} • ~${Formatter.formatFileSize(context, estimatedSavings)} potential savings",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    icon = Icons.Outlined.AutoFixHigh,
                    thumbnails = optimizablePreview,
                    onReviewClick = {
                        App.instance.navController().navigate(Routes.OPTIMISE_IMAGES)
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { vm.restartScan() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = "Scan Again",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ScanResultCategoryCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    thumbnails: List<LocalFile>,
    onReviewClick: () -> Unit,
) {
    Card(
        onClick = onReviewClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // ── Thumbnail strip ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (thumbnails.isNotEmpty()) {
                    thumbnails.take(3).forEach { file ->
                        AsyncImage(
                            model = file.id,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                        )
                    }
                    // Fill remaining slots with placeholder if < 3 thumbnails
                    repeat((3 - thumbnails.size).coerceAtLeast(0)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.outlineVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }

            // ── Info + button ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Review & Clean button
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(
                        text = "Review & Clean",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
