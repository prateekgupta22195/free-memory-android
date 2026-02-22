package com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM

@Composable
fun FlatFileManagerDeleteComposable(vm: SelectableDeletableVM) {
    val selectedFileIds = remember { vm.selectedFiles }
    val showDeleteDialog = remember { vm.showDeleteDialog }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.navigationBars)
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                println("Delete button clicked, files: ${selectedFileIds.value.size}")
                vm.deleteFiles(selectedFileIds.value)
            },
            enabled = selectedFileIds.value.isNotEmpty(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = if (selectedFileIds.value.isEmpty()) "Delete" else "Delete ${selectedFileIds.value.size} Files")
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        PopupCompose(show = true, onPopupDismissed = { vm.cancelDelete() }) {
            AlertDialog(
                onDismissRequest = { vm.cancelDelete() },
                title = { Text("Delete Files") },
                text = { 
                    Text("Are you sure you want to delete ${selectedFileIds.value.size} files? You will not be able to recover them.") 
                },
                confirmButton = {
                    TextButton(onClick = { 
                        println("Confirm delete clicked")
                        vm.confirmDeleteFiles() 
                    }) { 
                        Text("Delete", color = Color.Red) 
                    }
                },
                dismissButton = { TextButton(onClick = { 
                    println("Cancel delete clicked")
                    vm.cancelDelete() 
                }) { Text("Cancel") } }
            )
        }
    }
}