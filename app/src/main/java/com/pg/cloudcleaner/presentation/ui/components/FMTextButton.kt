package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FNTextButton(onClick: () -> Unit, text: String) {


    TextButton(onClick = { /*TODO*/ }) {
        
    }
    Box(modifier = Modifier.padding(8.dp)) {
        Text(text, modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth(), color = Color.Blue)
    }

}