package com.pg.cloudcleaner.ui

import androidx.compose.ui.graphics.Color

interface LightThemeColors : ThemeColors {

    companion object {
        val primary = Color(0XFFFFFFFF)
        val primaryVariant = Color(0XFFEEEEEE)
        val onPrimary = Color(0XFF222222)
    }
}

interface DarkThemeColors {

    companion object {
        val primary = Color(0XFF333333)
        val primaryVariant = Color(0XFF222222)
        val onPrimary = Color(0XFFEEEEEE)
    }
}

interface ThemeColors {

    val primary: Color
    val primaryVariant: Color
}
