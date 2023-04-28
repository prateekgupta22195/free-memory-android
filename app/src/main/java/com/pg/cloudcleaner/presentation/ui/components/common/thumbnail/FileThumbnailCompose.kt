package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.runtime.Composable
import com.pg.cloudcleaner.utils.isFileImage
import com.pg.cloudcleaner.utils.isFileVideo

@Composable
fun FileThumbnailCompose(mimeType: String?, model: Any) {
    return if (isFileImage(mimeType)) ImageThumbnailCompose(model = model)
    else if (isFileVideo(mimeType)) VideoThumbnailCompose(model = model)
    else OtherFileThumbnailCompose(mimeType = mimeType)
}





