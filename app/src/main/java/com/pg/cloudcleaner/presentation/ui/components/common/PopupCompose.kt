package com.pg.cloudcleaner.presentation.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.PopupProperties


@Composable
fun PopupCompose(
    show: Boolean,
    onPopupDismissed: (() -> Unit)? = null,
    popUpBody: @Composable () -> Unit,
) {
    BackHandler(enabled = show) {
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


