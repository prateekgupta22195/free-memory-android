package com.pg.cloudcleaner.presentation.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight

@Composable
fun CategoryWrapperCompose(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(contentPadding)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
            )
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Navigate to $title",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        }
    }
    
    // Content below the header row
    Column(modifier = Modifier) {
        content()
    }
}
