package com.pg.cloudcleaner.presentation.vm


import com.pg.cloudcleaner.utils.StorageInfo

/**
 * Represents the different states for the storage information UI.
 */
sealed interface StorageUiState {
    /**
     * The state while data is being fetched.
     */
    data object Loading : StorageUiState

    /**
     * The state when storage data has been successfully loaded.
     * @param info The loaded storage information.
     */
    data class Success(val info: StorageInfo) : StorageUiState

    /**
     * The state when an error occurs during data fetching.
     * @param message A descriptive error message.
     */
    data class Error(val message: String) : StorageUiState
}
