package com.pg.cloudcleaner.presentation.ui.components.common.pdf

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class ScrollableImpl : Scrollable, GestureDetector.OnGestureListener {
    private lateinit var view: View

    private lateinit var detector: GestureDetector

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent, x: Float, y: Float): Boolean {
        view.scrollBy(x.toInt(), y.toInt())
        return true
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent) {

    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }


    override fun onLongPress(p0: MotionEvent) {}

    override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun register(context: Context, view: View) {
        this.view = view
        detector = GestureDetector(context, this)
    }

    override fun onTouchEvent(event: MotionEvent) {
        detector.onTouchEvent(event)
    }
}