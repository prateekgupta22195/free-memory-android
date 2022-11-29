package com.pg.cloudcleaner.misc.vm

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pg.cloudcleaner.misc.data.repo.FileActionRepoImpl
import com.pg.cloudcleaner.misc.model.DriveFile
import com.pg.cloudcleaner.utils.getGoogleAccessToken
import com.pg.cloudcleaner.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileExplorerViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var filesMutable: Flow<List<DriveFile>?>

    private val fileRepo = FileActionRepoImpl(context = application.baseContext)

    var refreshing = mutableStateOf(false)

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    setRefreshing(true)
                    val accessToken = getGoogleAccessToken(context = getApplication<Application>().applicationContext)
                    fileRepo.syncDBFiles(accessToken = accessToken)
                    setRefreshing(false)
                } catch (e: Exception) {
                    getApplication<Application>().applicationContext.showToast(
                        e.localizedMessage
                            ?: "Something went wrong, Please try again later!"
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filesMutable = fileRepo.getAllFilesOwnedByMe()
            }
        }
    }

    private fun setRefreshing(state: Boolean) {
        viewModelScope.launch {
            refreshing.value = state
        }
    }
}
