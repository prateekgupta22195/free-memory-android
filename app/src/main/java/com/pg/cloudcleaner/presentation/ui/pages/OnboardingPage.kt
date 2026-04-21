package com.pg.cloudcleaner.presentation.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.pg.cloudcleaner.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val bgStart: Color,
    val bgEnd: Color,
    val phoneContent: PhoneContent,
)

private sealed interface PhoneContent {
    data class AppUI(
        val screenTitle: String,
        val accentColor: Color,
        val statLabel: String,
        val statValue: String,
        val statSubtext: String,
    ) : PhoneContent

    data class IconDisplay(
        val icon: ImageVector,
        val iconBg: Color,
        val mainText: String,
        val subText: String = "",
    ) : PhoneContent

    data class DuplicateCard(
        val fileCount: String,
        val totalSize: String,
    ) : PhoneContent

    data class LargeFilesCard(
        val totalSize: String,
        val fileCount: String,
    ) : PhoneContent

    object MediaOptimizerCard : PhoneContent
    object PrivacyGuardCard : PhoneContent
    object TrulyFreeCard : PhoneContent
    object OfflineFirstCard : PhoneContent
    object FamilySafeCard : PhoneContent
    object NativeThemeCard : PhoneContent
}

@Composable
private fun buildSlides() = listOf(
    OnboardingSlide(
        title = stringResource(R.string.onboarding_duplicate_title),
        subtitle = stringResource(R.string.onboarding_duplicate_subtitle),
        bgStart = Color(0xFF3A52F5),
        bgEnd = Color(0xFF1F38CC),
        phoneContent = PhoneContent.DuplicateCard(
            fileCount = "127",
            totalSize = "2.4 GB",
        ),
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_large_files_title),
        subtitle = stringResource(R.string.onboarding_large_files_subtitle),
        bgStart = Color(0xFF9B27AF),
        bgEnd = Color(0xFF6A1B9A),
        phoneContent = PhoneContent.LargeFilesCard(
            totalSize = "2.2 GB",
            fileCount = "34",
        ),
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_media_optimizer_title),
        subtitle = stringResource(R.string.onboarding_media_optimizer_subtitle),
        bgStart = Color(0xFF27AE60),
        bgEnd = Color(0xFF1B7D45),
        phoneContent = PhoneContent.MediaOptimizerCard,
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_privacy_title),
        subtitle = stringResource(R.string.onboarding_privacy_subtitle),
        bgStart = Color(0xFF1A2035),
        bgEnd = Color(0xFF0D1322),
        phoneContent = PhoneContent.PrivacyGuardCard,
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_free_title),
        subtitle = stringResource(R.string.onboarding_free_subtitle),
        bgStart = Color(0xFFE05252),
        bgEnd = Color(0xFFC0302B),
        phoneContent = PhoneContent.TrulyFreeCard,
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_offline_title),
        subtitle = stringResource(R.string.onboarding_offline_subtitle),
        bgStart = Color(0xFF29ABE2),
        bgEnd = Color(0xFF0D7FC0),
        phoneContent = PhoneContent.OfflineFirstCard,
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_family_title),
        subtitle = stringResource(R.string.onboarding_family_subtitle),
        bgStart = Color(0xFFE91E8C),
        bgEnd = Color(0xFF9C27B0),
        phoneContent = PhoneContent.FamilySafeCard,
    ),
    OnboardingSlide(
        title = stringResource(R.string.onboarding_theme_title),
        subtitle = stringResource(R.string.onboarding_theme_subtitle),
        bgStart = Color(0xFF1A237E),
        bgEnd = Color(0xFF0D47A1),
        phoneContent = PhoneContent.NativeThemeCard,
    ),
)

@Composable
fun OnboardingPage(onFinished: () -> Unit) {
    val slides = buildSlides()
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    // Interpolate gradient colors as the user swipes between slides
    val currentPage = pagerState.currentPage
    val offsetFraction = pagerState.currentPageOffsetFraction
    val targetPage = if (offsetFraction >= 0f) {
        (currentPage - 1).coerceAtLeast(0)
    } else {
        (currentPage + 1).coerceAtMost(slides.size - 1)
    }
    val fraction = kotlin.math.abs(offsetFraction).coerceIn(0f, 1f)
    val bgStart = lerp(slides[currentPage].bgStart, slides[targetPage].bgStart, fraction)
    val bgEnd = lerp(slides[currentPage].bgEnd, slides[targetPage].bgEnd, fraction)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgStart, bgEnd))),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            SlideContent(slide = slides[page])
        }

        val isLast = pagerState.currentPage == slides.size - 1
        if (!isLast) {
            TextButton(
                onClick = { onFinished() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.action_skip),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(slides.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) Color.White else Color.White.copy(alpha = 0.4f)
                            ),
                    )
                }
            }

            Button(
                onClick = {
                    if (isLast) {
                        onFinished()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            ) {
                Text(
                    text = if (isLast) stringResource(R.string.action_get_started) else stringResource(R.string.action_next),
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun SlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp, bottom = 180.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            Text(
                text = slide.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = slide.subtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(32.dp))
            PhoneMockup(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 260.dp),
                content = slide.phoneContent,
            )
    }
}

@Composable
private fun PhoneMockup(modifier: Modifier = Modifier, content: PhoneContent) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF0D0D1A))
            .padding(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "9:41 AM",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Wifi, null, tint = Color.White, modifier = Modifier.size(10.dp))
                Icon(Icons.Filled.BatteryFull, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }

        when (content) {
            is PhoneContent.AppUI -> AppUIContent(content)
            is PhoneContent.IconDisplay -> IconDisplayContent(content)
            is PhoneContent.DuplicateCard -> DuplicateCardContent(content)
            is PhoneContent.LargeFilesCard -> LargeFilesCardContent(content)
            PhoneContent.MediaOptimizerCard -> MediaOptimizerCardContent()
            PhoneContent.PrivacyGuardCard -> PrivacyGuardCardContent()
            PhoneContent.TrulyFreeCard -> TrulyFreeCardContent()
            PhoneContent.OfflineFirstCard -> OfflineFirstCardContent()
            PhoneContent.FamilySafeCard -> FamilySafeCardContent()
            PhoneContent.NativeThemeCard -> NativeThemeCardContent()
        }
    }
}

@Composable
private fun DuplicateCardContent(content: PhoneContent.DuplicateCard) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
        ) {
            // Photo grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Brush.verticalGradient(listOf(Color(0xFF4A7C59), Color(0xFF2E5E3E)))),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Brush.verticalGradient(listOf(Color(0xFF0E4D8C), Color(0xFF1A7DC4)))),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Brush.verticalGradient(listOf(Color(0xFF1A0A2E), Color(0xFF6A2085)))),
                )
            }

            // Info row
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFE8F0FE), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = null,
                        tint = Color(0xFF2F6FED),
                        modifier = Modifier.size(15.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.onboarding_duplicate_photos_label),
                        color = Color(0xFF1A1A1A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${content.fileCount} files • ${content.totalSize}",
                        color = Color(0xFF666666),
                        fontSize = 8.sp,
                    )
                }
            }

            // Button
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF2F6FED), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.review_clean_button),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp),
                    )
                }
            }
        }
    }
}

private data class LargeFileRow(val icon: ImageVector, val iconTint: Color, val name: String, val size: String)

@Composable
private fun LargeFilesCardContent(content: PhoneContent.LargeFilesCard) {
    val files = listOf(
        LargeFileRow(Icons.Filled.Movie, Color(0xFFE53935), "vacation_2024.mp4", "1.2 GB"),
        LargeFileRow(Icons.Filled.Folder, Color(0xFFFF8F00), "project_backup.zip", "650 MB"),
        LargeFileRow(Icons.Filled.Image, Color(0xFF6A1B9A), "raw_photos.tar", "380 MB"),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.category_large_files),
                    color = Color(0xFF1A1A1A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF3E5F5), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = content.totalSize,
                        color = Color(0xFF7B1FA2),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // File rows
            files.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = file.icon,
                            contentDescription = null,
                            tint = file.iconTint,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = file.name,
                            color = Color(0xFF333333),
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = file.size,
                        color = Color(0xFF888888),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            // Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8E24AA), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_find_large_files),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaOptimizerCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(stringResource(R.string.onboarding_image_optimiser_label), color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            // Before / After row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Brush.verticalGradient(listOf(Color(0xFF4A7C59), Color(0xFF2E5E3E)))),
                    )
                    Text(stringResource(R.string.onboarding_original_label), color = Color(0xFF888888), fontSize = 7.sp)
                    Text("2.4 MB", color = Color(0xFFE53935), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color(0xFF27AE60), modifier = Modifier.size(18.dp).align(Alignment.CenterVertically))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Brush.verticalGradient(listOf(Color(0xFF4A7C59), Color(0xFF2E5E3E)))),
                    )
                    Text(stringResource(R.string.onboarding_optimised_label), color = Color(0xFF888888), fontSize = 7.sp)
                    Text("580 KB", color = Color(0xFF27AE60), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Savings bar
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.onboarding_space_saved), color = Color(0xFF888888), fontSize = 7.sp)
                    Text("76%", color = Color(0xFF27AE60), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.76f)
                            .fillMaxHeight()
                            .background(Color(0xFF27AE60)),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF27AE60), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.onboarding_optimise_now), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}

@Composable
private fun PrivacyGuardCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
        ) {
            // Dark header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1322))
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).background(Color(0xFF2F80ED), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.VerifiedUser, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Text(stringResource(R.string.onboarding_100_local), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Feature rows
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(stringResource(R.string.onboarding_no_internet), stringResource(R.string.onboarding_no_data_collection), stringResource(R.string.onboarding_no_ad_tracking)).forEach { label ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF27AE60), modifier = Modifier.size(14.dp))
                        Text(label, color = Color(0xFF333333), fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrulyFreeCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // FREE badge
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFF3F3), RoundedCornerShape(50))
                    .padding(horizontal = 20.dp, vertical = 6.dp),
            ) {
                Text(stringResource(R.string.onboarding_100_free), color = Color(0xFFE05252), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(stringResource(R.string.onboarding_free_forever), color = Color(0xFF888888), fontSize = 8.sp)

            // No X items
            listOf(stringResource(R.string.onboarding_no_advertisements), stringResource(R.string.onboarding_no_subscription), stringResource(R.string.onboarding_no_hidden_costs)).forEach { label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Block, null, tint = Color(0xFFE05252), modifier = Modifier.size(12.dp))
                    Text(label, color = Color(0xFF333333), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun OfflineFirstCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Wifi-off badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1E2540), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.WifiOff, null, tint = Color(0xFF29ABE2), modifier = Modifier.size(26.dp))
            }
            Text(stringResource(R.string.onboarding_works_anywhere), color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            // Location rows
            listOf(
                Triple(Icons.Filled.LocationOn, Color(0xFF29ABE2), stringResource(R.string.onboarding_inflight)),
                Triple(Icons.Filled.LocationOn, Color(0xFF27AE60), stringResource(R.string.onboarding_remote_outdoors)),
                Triple(Icons.Filled.LocationOn, Color(0xFF9B27AF), stringResource(R.string.onboarding_underground)),
            ).forEach { (icon, tint, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF4F9FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(icon, null, tint = tint, modifier = Modifier.size(12.dp))
                    Text(label, color = Color(0xFF333333), fontSize = 9.sp)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF27AE60), modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
private fun FamilySafeCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier.size(28.dp).background(Color(0xFFFFE0F0), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Security, null, tint = Color(0xFFE91E8C), modifier = Modifier.size(16.dp))
                }
                Text(stringResource(R.string.onboarding_family_policy), color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Star rating
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                }
            }

            // Safety items
            listOf(stringResource(R.string.onboarding_safe_all_ages), stringResource(R.string.onboarding_no_data_collection), stringResource(R.string.onboarding_no_risky_permissions), stringResource(R.string.onboarding_no_ads_or_purchases)).forEach { label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF0F8), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFFE91E8C), modifier = Modifier.size(11.dp))
                    Text(label, color = Color(0xFF333333), fontSize = 8.sp)
                }
            }
        }
    }
}

@Composable
private fun NativeThemeCardContent() {
    Box(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
        ) {
            // Light / Dark split
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
            ) {
                // Light side
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFF5F7FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.WifiOff, null, tint = Color(0xFFFFB300), modifier = Modifier.size(22.dp))
                        Text(stringResource(R.string.onboarding_light_mode), color = Color(0xFF444444), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
                // Divider
                Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFFE0E0E0)))
                // Dark side
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF1A1F2E)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.DarkMode, null, tint = Color(0xFF90CAF9), modifier = Modifier.size(22.dp))
                        Text(stringResource(R.string.onboarding_dark_mode), color = Color(0xFFCCCCCC), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Footer
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(stringResource(R.string.onboarding_follows_system_theme), color = Color(0xFF1A1A1A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                listOf(stringResource(R.string.onboarding_material_you), stringResource(R.string.onboarding_tablet_optimised)).forEach { label ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF1A237E), modifier = Modifier.size(11.dp))
                        Text(label, color = Color(0xFF666666), fontSize = 8.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUIContent(content: PhoneContent.AppUI) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = content.screenTitle,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(content.accentColor)
                .padding(12.dp),
        ) {
            Column {
                Text(
                    text = content.statLabel.uppercase(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = content.statValue,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (content.statSubtext.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = content.statSubtext,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 8.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun IconDisplayContent(content: PhoneContent.IconDisplay) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(content.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = content.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = content.mainText,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
        if (content.subText.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = content.subText,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
