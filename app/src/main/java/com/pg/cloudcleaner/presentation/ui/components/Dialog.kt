package com.pg.cloudcleaner.presentation.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.PopupProperties


@Composable
fun Popup(
    show: Boolean,
    onPopupDismissed: (() -> Unit)? = null,
    popUpBody: @Composable () -> Unit,
) {

    val popupVisibility = remember { show }

    BackHandler(enabled = popupVisibility) {
        onPopupDismissed?.invoke()
    }

    if (show) {
        androidx.compose.ui.window.Popup(
            content = popUpBody, alignment = Alignment.Center, onDismissRequest = {
                onPopupDismissed?.invoke()
            }, properties = PopupProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }

}


