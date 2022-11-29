package com.pg.cloudcleaner.helper

interface FileAction {

    suspend fun readAllFilesInTheDirectory(directoryName: String)

    fun deleteFile(fileIdentity: String)

}