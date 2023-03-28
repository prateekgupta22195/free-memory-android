package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.pg.cloudcleaner.presentation.ui.pages.imageModifier
import com.pg.cloudcleaner.presentation.ui.pages.paddingModifier

@Composable
fun FileItem(
    id: String,
    onClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable {
        onClick()
    }) {
        Card(modifier = paddingModifier) {
            Image(
                model = id,
                contentScale = ContentScale.Inside,
                contentDescription = "",
                modifier = imageModifier,
            )
        }
    }
}