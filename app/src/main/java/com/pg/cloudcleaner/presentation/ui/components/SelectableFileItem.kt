package com.pg.cloudcleaner.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pg.cloudcleaner.data.model.LocalFile

@Composable
fun SelectableFileItem(file: LocalFile) {


    Box {

        FileItem(file = file, onClick = {

        })

        Checkbox(checked = false, onCheckedChange = {

        }, modifier = Modifier.align(Alignment.TopEnd))

    }

}