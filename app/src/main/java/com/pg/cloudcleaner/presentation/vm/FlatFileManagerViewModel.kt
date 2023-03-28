package com.pg.cloudcleaner.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class FlatFileManagerViewModel : ViewModel() {

    private val action: FileActionInteractor =
        FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))

    fun readFiles(): Flow<Map<String, List<LocalFile>>> {
        return action.getMediaFiles().flowOn(Dispatchers.IO).map {
            it.groupBy { localFile ->
                localFile.md5CheckSum
            }
        }
//         TODO: revert
//        return action.getMediaFiles().flowOn(Dispatchers.IO)
    }


    fun deleteFile(localFile: LocalFile) {
        viewModelScope.launch(Dispatchers.IO) {

//            deleting file from local storage table
            launch { action.deleteFile(localFile.id) }.join()

//            deleting file from directory
            File(localFile.id).apply {
                if (exists())
                    delete()
            }
        }
    }
}
