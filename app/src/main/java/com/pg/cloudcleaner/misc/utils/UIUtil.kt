package com.pg.cloudcleaner.misc.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Fragment.contentView(
    compositionStrategy: ViewCompositionStrategy = ViewCompositionStrategy.DisposeOnDetachedFromWindow,
    context: Context? = getContext(),
    content: @Composable () -> Unit,
): ComposeView? {
    context ?: return null
    val view = ComposeView(context)
    view.setViewCompositionStrategy(compositionStrategy)
    view.setContent(content)
    return view
}


