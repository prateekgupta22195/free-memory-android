package com.pg.cloudcleaner.presentation.ui.components.thumbnail

import androidx.compose.runtime.Composable
import com.pg.cloudcleaner.utils.isFileImage
import com.pg.cloudcleaner.utils.isFileVideo

@Composable
fun FileThumbnail(mimeType: String?, model: Any) {
    return if (isFileImage(mimeType)) ImageThumbnail(model = model)
    else if (isFileVideo(mimeType)) VideoThumbnail(model = model)
    else OtherFileThumbnail(mimeType = mimeType)
}