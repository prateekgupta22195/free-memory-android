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

            // Phase 1: compress files, track results — no DB writes yet
            data class OptimiseResult(val filePath: String, val newSizeKb: Long?)
            var totalSaved = 0L
            val results = mutableListOf<OptimiseResult>()
            toOptimise.forEach { filePath ->
                val saved = ImageOptimizer.optimize(filePath)
                if (saved > 0L) {
                    totalSaved += saved
                    results.add(OptimiseResult(filePath, File(filePath).length() / 1024))
                } else {
                    results.add(OptimiseResult(filePath, null))
                }
                withContext(Dispatchers.Main) { optimisedCount.intValue++ }
            }

            // Phase 2: single transaction → one Room invalidation for the whole batch
            fileUseCases.applyOptimisationResults(results.map { it.filePath to it.newSizeKb })

            SavedMemoryTracker.addSavedBytes(totalSaved)

            withContext(Dispatchers.Main) {
                isOptimising.value = false
                showConfirmDialog.value = false
                selectedFileIds.value = emptySet()
            }
        }
    }
}
