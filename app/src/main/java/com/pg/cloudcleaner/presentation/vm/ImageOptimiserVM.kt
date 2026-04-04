package com.pg.cloudcleaner.presentation.vm

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileUseCases
import com.pg.cloudcleaner.utils.ImageOptimizer
import com.pg.cloudcleaner.utils.SavedMemoryTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageOptimiserVM : ViewModel() {

    private val fileUseCases = FileUseCases(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    val images: StateFlow<List<LocalFile>?> = fileUseCases.getOptimizableImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedFileIds = mutableStateOf(emptySet<String>())
    val showConfirmDialog = mutableStateOf(false)
    val isOptimising = mutableStateOf(false)
    val optimisedCount = mutableIntStateOf(0)
    val totalToOptimise = mutableIntStateOf(0)

    // Called once when the list first loads to pre-select everything
    fun initSelection(files: List<LocalFile>) {
        if (selectedFileIds.value.isEmpty()) {
            selectedFileIds.value = files.map { it.id }.toSet()
        }
    }

    fun toggleSelection(id: String) {
        selectedFileIds.value = if (id in selectedFileIds.value)
            selectedFileIds.value - id
        else
            selectedFileIds.value + id
    }

    fun toggleAll(files: List<LocalFile>) {
        val allIds = files.map { it.id }.toSet()
        selectedFileIds.value =
            if (selectedFileIds.value.containsAll(allIds)) emptySet() else allIds
    }

    fun requestOptimise() {
        showConfirmDialog.value = true
    }

    fun cancelOptimise() {
        showConfirmDialog.value = false
    }

    fun confirmOptimise() {
        viewModelScope.launch(Dispatchers.IO) {
            val toOptimise = selectedFileIds.value.toList()
            withContext(Dispatchers.Main) {
                isOptimising.value = true
                totalToOptimise.intValue = toOptimise.size
                optimisedCount.intValue = 0
            }

            var totalSaved = 0L
            toOptimise.forEach { filePath ->
                val saved = ImageOptimizer.optimize(filePath)
                if (saved > 0L) {
                    totalSaved += saved
                    val newSizeKb = File(filePath).length() / 1024
                    fileUseCases.updateFileSize(filePath, newSizeKb)
                }
                fileUseCases.markFileOptimized(filePath)
                withContext(Dispatchers.Main) { optimisedCount.intValue++ }
            }

            SavedMemoryTracker.addSavedBytes(totalSaved)

            withContext(Dispatchers.Main) {
                isOptimising.value = false
                showConfirmDialog.value = false
                selectedFileIds.value = emptySet()
            }
        }
    }
}
