package com.pg.cloudcleaner.presentation



sealed interface WorkerUIState {


    data class InProgress(val message: String, val progress: Int = 0) : WorkerUIState

    data class Success(val message: String) : WorkerUIState

    data class Failed(val message: String) : WorkerUIState

    data class Cancelled(val message: String) : WorkerUIState

}