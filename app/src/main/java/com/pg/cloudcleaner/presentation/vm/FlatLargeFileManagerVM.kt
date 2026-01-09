package com.pg.cloudcleaner.presentation.vm


class FlatLargeFileManagerVM : SelectableDeletableVM() {
    fun getLargeFiles() = fileUseCases.getLargeFiles()
}