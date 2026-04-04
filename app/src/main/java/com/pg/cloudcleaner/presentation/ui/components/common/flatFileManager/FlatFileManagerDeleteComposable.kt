package com.pg.cloudcleaner.presentation.ui.components.common.flatFileManager

import android.text.format.Formatter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.presentation.ui.components.common.PopupCompose
import com.pg.cloudcleaner.presentation.vm.SelectableDeletableVM

@Composable
fun FlatFileManagerDeleteComposable(vm: SelectableDeletableVM, selectedSize: Long = 0L) {
    val context = LocalContext.current
    val selectedFiles = remember { vm.selectedFiles }
    val showDeleteDialog = remember { vm.showDeleteDialog }
    val isDeleting = remember { vm.isDeleting }
    val formattedSize = Formatter.formatFileSize(context, selectedSize)

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { vm.deleteFiles(selectedFiles.value) },
            enabled = selectedFiles.value.isNotEmpty() && !isDeleting.value,
            modifier = Modifier.align(Alignment.Center),
        ) {
            if (selectedFiles.value.isEmpty()) {
                Text("Delete")
            } else {
                Text("Delete ${selectedFiles.value.size} files · $formattedSize")
            }
        }
    }

    if (showDeleteDialog.value || isDeleting.value) {
        PopupCompose(show = true, onPopupDismissed = { if (!isDeleting.value) vm.cancelDelete() }) {
            AlertDialog(
                onDismissRequest = { if (!isDeleting.value) vm.cancelDelete() },
                title = { Text(if (isDeleting.value) "Deleting Files" else "Delete Files") },
                text = {
                    if (isDeleting.value) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Deleting ${selectedFiles.value.size} files, please wait...")
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Text("Are you sure you want to delete ${selectedFiles.value.size} files ($formattedSize)? You will not be able to recover them.")
                    }
                },
                confirmButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = { vm.confirmDeleteFiles() }) {
                            Text("Delete", color = Color.Red)
                        }
                    }
                },
                dismissButton = {
                    if (!isDeleting.value) {
                        TextButton(onClick = { vm.cancelDelete() }) { Text("Cancel") }
                    }
                }
            )
        }
    }
}
