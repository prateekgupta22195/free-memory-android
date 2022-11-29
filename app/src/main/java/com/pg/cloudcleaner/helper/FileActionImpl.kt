package com.pg.cloudcleaner.helper

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class FileActionImpl(val list: SnapshotStateList<File>) : FileAction {

    override suspend fun readAllFilesInTheDirectory(
        directoryName: String,
    ) {
        exploreDirectory(directoryName)
    }

    override fun deleteFile(fileIdentity: String) {
        /**
         * No-op
         */
    }

    private suspend fun exploreDirectory(directoryPath: String) {
        coroutineScope {
            launch {
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
        }
    }

}