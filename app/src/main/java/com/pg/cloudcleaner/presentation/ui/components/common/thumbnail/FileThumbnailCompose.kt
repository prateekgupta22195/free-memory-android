package com.pg.cloudcleaner.presentation.ui.components.common.thumbnail

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pg.cloudcleaner.utils.isImage
import com.pg.cloudcleaner.utils.isVideo

@Composable
fun FileThumbnailCompose(
    model: Any,
    modifier: Modifier = Modifier,
) {

    val modifierWithClip = modifier.clip(RoundedCornerShape(8.dp))
    val filePath = model.toString()

    if (isImage(filePath)) {
        ImageThumbnailCompose(model = model)
    } else if (isVideo(filePath)) {
        VideoThumbnailCompose(model = model)
    } else {
        OtherFileThumbnailCompose(
            modifier = modifierWithClip,
            filePath = filePath
        )
    }
}
