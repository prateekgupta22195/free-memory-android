package com.pg.cloudcleaner.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class FlatFileManagerViewModel(encodedDirectoryPath: String) : ViewModel() {

    private val directoryPath = URLDecoder.decode(encodedDirectoryPath, StandardCharsets.UTF_8.toString())
    val list: MutableList<File> = mutableStateListOf()
    init {
        exploreDirectory(directoryPath = directoryPath)
    }

    private fun exploreDirectory(directoryPath: String) {
        val directory = File(directoryPath)
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory)
                exploreDirectory(file.absolutePath)
            else {
                if (!file.name.startsWith('.')) {
                    list.add(file)
                }
            }
        }
    }

    class Factory(private val encodedDirectoryPath: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FlatFileManagerViewModel(encodedDirectoryPath) as T
        }
    }
}
