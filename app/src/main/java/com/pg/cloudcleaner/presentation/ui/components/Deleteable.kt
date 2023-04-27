package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Deleteable(
    content: @Composable (PaddingValues) -> Unit,
    deleteButton: @Composable () -> Unit,
    pageTitle: String,
    actions: @Composable RowScope.() -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(title = { Text(pageTitle) },
            actions = actions,
            navigationIcon = { BackNavigationIcon() })
    }, content = content, bottomBar = deleteButton)
}