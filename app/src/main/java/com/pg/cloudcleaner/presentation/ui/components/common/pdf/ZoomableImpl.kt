package com.pg.cloudcleaner.presentation.ui.components.common.pdf

import android.content.Context
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView

class ZoomableImpl : Zoomable, ScaleGestureDetector.OnScaleGestureListener {

    private var scaleFactor = 1.0f
    private lateinit var view: View
    private lateinit var detector: ScaleGestureDetector
    private val minScaleFactor = 0.5f
    private val maxScaleFactor = 5.0f

    override fun onScale(p0: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor

        // Restrict the scale factor within a desired range
        scaleFactor = scaleFactor.coerceIn(
            minScaleFactor,
            maxScaleFactor
        )
        // Apply the scale factor to the ImageView
        view.scaleX = scaleFactor
        view.scaleY = scaleFactor
        return true
    }

    override fun onScaleBegin(p0: ScaleGestureDetector): Boolean { return true }

    override fun onScaleEnd(p0: ScaleGestureDetector) {}

    override fun register(context: Context, view: View) {
        this.view = view
        detector = ScaleGestureDetector(context, this)
    }

    override fun onTouchEvent(event: MotionEvent) {
        detector.onTouchEvent(event)
    }

    override fun updateImageMatrix() {
        val matrix = Matrix()
        matrix.setScale(scaleFactor, scaleFactor)
        (view as ImageView).imageMatrix = matrix
    }
}