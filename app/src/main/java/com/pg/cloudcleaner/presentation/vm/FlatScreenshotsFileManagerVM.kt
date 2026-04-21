package com.pg.cloudcleaner.presentation.vm

class FlatScreenshotsFileManagerVM : SelectableDeletableVM() {
    fun getScreenshotFiles() = fileUseCases.getScreenshotFiles()
}
