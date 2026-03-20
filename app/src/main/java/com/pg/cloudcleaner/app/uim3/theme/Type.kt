package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.pg.cloudcleaner.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val ArimoFont = GoogleFont("Arimo")

val ArimoFontFamily = FontFamily(
    Font(googleFont = ArimoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = ArimoFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = ArimoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = ArimoFont, fontProvider = provider, weight = FontWeight.Bold),
)

val AppTypography = Typography(
    // Display — large stat numbers (e.g. 4.7 GB hero)
    displayLarge = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,        // "4.7 GB" hero stat
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    // Headline — screen titles and section headers
    headlineLarge = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,        // "Great Job!", "Scan Complete"
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,        // "Analyzing Storage"
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Title — card headers and category names
    titleLarge = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,        // "Found So Far", "Categories"
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,        // category item titles (e.g. "Duplicate Photos")
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Body — descriptive and secondary text
    bodyLarge = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,        // "Scanning your device..."
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,        // "Finding unnecessary files...", file counts
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,        // secondary labels (e.g. "Duplicates", "Screenshots")
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Label — buttons and chips
    labelLarge = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,        // "Back to Home", "Clean More Files", "Review & Clean"
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,        // "Start Cleaning"
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ArimoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
)
