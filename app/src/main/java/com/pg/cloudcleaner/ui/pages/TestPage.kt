package com.pg.cloudcleaner.ui.pages

import android.os.Environment
import androidx.compose.runtime.Composable
import com.pg.cloudcleaner.utils.LogCompositions
import timber.log.Timber

@Composable
fun TestPage() {
    LogCompositions(msg = "TestPage")

    val files = Environment.getExternalStorageDirectory().listFiles()

    Timber.d("root directory path ${Environment.getExternalStorageDirectory().absolutePath}")

    files.forEach { file ->
        Timber.d("file name ${file.name}")
    }
}
