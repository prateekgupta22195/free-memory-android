package com.pg.cloudcleaner.presentation.ui.components.common.pdf

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class PdfRendererView(context: Context) : androidx.appcompat.widget.AppCompatImageView(context) {

    private var zoomable: Zoomable
    private var scrollable: Scrollable

    init {
        zoomable = ZoomableImpl().apply {
            register(context = context, this@PdfRendererView)
        }
        scrollable = ScrollableImpl().apply {
            register(context = context, this@PdfRendererView)
        }
    }

    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null

    fun loadPdfFile(filePath: String) {
        val fileDescriptor =
            ParcelFileDescriptor.open(File(filePath), ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor!!)
        openPage(0)
        fileDescriptor.close()
    }

    private fun openPage(pageIndex: Int) {
        currentPage?.close()
        currentPage = pdfRenderer?.openPage(pageIndex)

        val bitmap =
            Bitmap.createBitmap(
                currentPage!!.width,
                currentPage!!.height,
                Bitmap.Config.ARGB_8888
            )

        currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        setImageBitmap(bitmap)
        zoomable.updateImageMatrix()
    }

    fun getImageAspectRatio(): Float {

        return (currentPage?.width?.toFloat() ?: 1f) / (currentPage?.height?.toFloat() ?: 1f)
    }

    fun closePdfRenderer() {
        currentPage?.close()
        pdfRenderer?.close()
    }

    private var canvas: Canvas? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        drawAnnotations()
    }

    private fun drawAnnotations() {
        // Ensure the canvas and current page are not null

        GlobalScope.launch {
            canvas?.let { canvas ->
                currentPage?.let { page ->
                    // Set up the paint for drawing
                    val paint = Paint().apply {
                        color = Color.RED
                        strokeWidth = 5f
                        style = Paint.Style.STROKE
                    }

                    // Draw a rectangle as an example annotation
                    val rect = RectF(100f, 100f, 200f, 200f)
                    canvas.drawRect(rect, paint)

                    // Call invalidate to trigger a redraw
//                invalidate()
                }
            }
        }

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
//        if(scaleGestureDetector.isInProgress) {
        zoomable.onTouchEvent(event)
        scrollable.onTouchEvent(event)
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }


}