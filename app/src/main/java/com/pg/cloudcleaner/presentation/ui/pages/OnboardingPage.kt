package com.pg.cloudcleaner.presentation.ui.pages

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
}

private fun buildSlides() = listOf(
    OnboardingSlide(
        title = "Duplicate Cleanup",
        subtitle = "Intelligently identify and remove redundant media files to free up storage instantly.",
        bgStart = Color(0xFF3A52F5),
        bgEnd = Color(0xFF1F38CC),
        phoneContent = PhoneContent.AppUI(
            screenTitle = "Duplicate Media",
            accentColor = Color(0xFF3A70F5),
            statLabel = "Potential Savings",
            statValue = "127.5 MB",
            statSubtext = "Found 48 duplicate files",
        ),
    ),
    OnboardingSlide(
        title = "Large File Manager",
        subtitle = "Find massive zip files, videos, and downloads that are taking up precious gigabytes.",
        bgStart = Color(0xFF9B27AF),
        bgEnd = Color(0xFF6A1B9A),
        phoneContent = PhoneContent.AppUI(
            screenTitle = "Large Files",
            accentColor = Color(0xFF9B27AF),
            statLabel = "Total Large Files",
            statValue = "2.2 GB",
            statSubtext = "Freeing this space will optimize your device",
        ),
    ),
    OnboardingSlide(
        title = "Media Optimizer",
        subtitle = "Identify oversized images and videos. Keep your memories, lose the storage bloat.",
        bgStart = Color(0xFF27AE60),
        bgEnd = Color(0xFF1B7D45),
        phoneContent = PhoneContent.AppUI(
            screenTitle = "Large Images",
            accentColor = Color(0xFF27AE60),
            statLabel = "Space Wasted",
            statValue = "54.9 MB",
            statSubtext = "",
        ),
    ),
    OnboardingSlide(
        title = "Privacy Guard",
        subtitle = "Your data stays on your tablet. No internet, no tracking. Total local security.",
        bgStart = Color(0xFF1A2035),
        bgEnd = Color(0xFF0D1322),
        phoneContent = PhoneContent.IconDisplay(
            icon = Icons.Filled.VerifiedUser,
            iconBg = Color(0xFF2F80ED),
            mainText = "Your Data,\nYour Privacy",
        ),
    ),
    OnboardingSlide(
        title = "Truly Free",
        subtitle = "No advertisements, no subscriptions, no hidden costs. A pure utility for the community.",
        bgStart = Color(0xFFE05252),
        bgEnd = Color(0xFFC0302B),
        phoneContent = PhoneContent.IconDisplay(
            icon = Icons.Filled.SentimentSatisfied,
            iconBg = Color(0xFF27AE60),
            mainText = "100% Free\nForever",
        ),
    ),
    OnboardingSlide(
        title = "Offline First",
        subtitle = "Clean your device anywhere, anytime. No active internet connection or data usage required.",
        bgStart = Color(0xFF29ABE2),
        bgEnd = Color(0xFF0D7FC0),
        phoneContent = PhoneContent.IconDisplay(
            icon = Icons.Filled.WifiOff,
            iconBg = Color(0xFF1E2540),
            mainText = "No Internet\nNeeded",
        ),
    ),
    OnboardingSlide(
        title = "Family Safe",
        subtitle = "Committed to Google Play Family Policy. Safe for all ages with no risky permissions.",
        bgStart = Color(0xFFE91E8C),
        bgEnd = Color(0xFF9C27B0),
        phoneContent = PhoneContent.IconDisplay(
            icon = Icons.Filled.Security,
            iconBg = Color(0xFFE91E63),
            mainText = "Play Family Policy",
            subText = "No data collection. No tracking.",
        ),
    ),
    OnboardingSlide(
        title = "Native Theme",
        subtitle = "Full support for light and dark modes. Designed to look stunning on high-resolution tablet displays.",
        bgStart = Color(0xFF1A237E),
        bgEnd = Color(0xFF0D47A1),
        phoneContent = PhoneContent.IconDisplay(
            icon = Icons.Filled.DarkMode,
            iconBg = Color(0xFF1565C0),
            mainText = "Native Dark Mode",
            subText = "A beautifully optimized dark mode",
        ),
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
                    text = "Skip",
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
                    text = if (isLast) "Get Started" else "Next",
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
