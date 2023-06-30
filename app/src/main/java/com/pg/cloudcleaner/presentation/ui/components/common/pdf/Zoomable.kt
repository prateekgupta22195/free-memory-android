package com.pg.cloudcleaner.presentation.ui.components.common.pdf

import android.content.Context
import android.view.MotionEvent
import android.view.View

interface Zoomable {
    fun register(context: Context, view: View)
    fun onTouchEvent(event: MotionEvent)
    fun updateImageMatrix()
}