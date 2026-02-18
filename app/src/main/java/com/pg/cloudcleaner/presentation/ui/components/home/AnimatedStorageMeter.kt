package com.pg.cloudcleaner.presentation.ui.components.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

private object StorageMeterAnimationSession {
    var hasAnimated: Boolean = false
}

/**
 * An animated linear progress meter to display storage usage, matching the app's modern UI.
 *
 * @param usedSpaceGB Used storage in Gigabytes.
 * @param totalSpaceGB Total storage in Gigabytes.
 * @param isLoading Whether the data is currently being loaded.
 * @param modifier Modifier for this composable.
 */
@Composable
fun AnimatedStorageMeter(
    usedSpaceGB: Float,
    totalSpaceGB: Float,
    isLoading: Boolean, // <-- Add isLoading parameter
    modifier: Modifier = Modifier
) {
    val usedPercentage = if (totalSpaceGB > 0) usedSpaceGB / totalSpaceGB else 0f
    val freeSpaceGB = totalSpaceGB - usedSpaceGB

    val animatedPercentage = remember { Animatable(0f) }
    val decimalFormat = remember { DecimalFormat("#.#") }

    LaunchedEffect(usedPercentage, isLoading) {
        if (isLoading) {
            animatedPercentage.snapTo(0f)
            return@LaunchedEffect
        }

        if (!StorageMeterAnimationSession.hasAnimated) {
            StorageMeterAnimationSession.hasAnimated = true
            animatedPercentage.animateTo(
                targetValue = usedPercentage,
                animationSpec = tween(durationMillis = 1800)
            )
        } else {
            animatedPercentage.snapTo(usedPercentage)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            val cornerRadius = CornerRadius(x = size.height / 2, y = size.height / 2)

            // Background track
            drawRoundRect(
                color = surfaceColor,
                size = size,
                cornerRadius = cornerRadius
            )

            // Foreground progress (animated)
            drawRoundRect(
                color = primaryColor,
                // Use animatedPercentage if not loading, otherwise 0
                size = Size(width = if (isLoading) 0f else size.width * animatedPercentage.value, height = size.height),
                cornerRadius = cornerRadius
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                // Show placeholder text while loading
                Text(text = "Used: – GB", fontSize = 14.sp, color = onSurfaceColor.copy(alpha = 0.6f))
                Text(text = "Free: – GB", fontSize = 14.sp, color = onSurfaceColor.copy(alpha = 0.6f))
            } else {
                // "Used" text
                Text(
                    text = buildAnnotatedString {
                        append("Used: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(decimalFormat.format(usedSpaceGB))
                            append(" GB")
                        }
                    },
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )

                // "Free" text
                Text(
                    text = buildAnnotatedString {
                        append("Free: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)) {
                            append(decimalFormat.format(freeSpaceGB))
                            append(" GB")
                        }
                    },
                    fontSize = 14.sp,
                    color = onSurfaceColor
                )
            }
        }
    }
}
