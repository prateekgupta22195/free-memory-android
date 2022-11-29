package com.pg.cloudcleaner.vm

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pg.cloudcleaner.helper.FileAction
import com.pg.cloudcleaner.helper.FileActionImpl
import kotlinx.coroutines.*
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class FlatFileManagerViewModel(encodedDirectoryPath: String) : ViewModel() {

    private val directoryPathDecoded =
        URLDecoder.decode(encodedDirectoryPath, StandardCharsets.UTF_8.toString())

    val list = mutableStateListOf<File>()

    private val action: FileAction = FileActionImpl(list)

    suspend fun readFiles(directoryPath: String = directoryPathDecoded) {
        action.readAllFilesInTheDirectory(directoryPath)
    }


    class Factory(private val encodedDirectoryPath: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FlatFileManagerViewModel(encodedDirectoryPath) as T
        }
    }
}
