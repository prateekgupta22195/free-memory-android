package com.pg.cloudcleaner.presentation.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.PopupProperties


@Composable
fun Popup(show: Boolean, onBackPress: () -> Unit, popUpBody: @Composable () -> Unit,) {

    BackHandler {
        onBackPress()
    }

    if (show) {
        androidx.compose.ui.window.Popup(
            content = popUpBody,
            alignment = Alignment.Center,

            properties = PopupProperties(dismissOnBackPress = true, dismissOnClickOutside = true, )
        )
    }

}


@Composable
fun ActionablePopup() {

}
