package com.pg.cloudcleaner.presentation.ui.components.common

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Looper
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.text.TextRenderer
import com.pg.cloudcleaner.presentation.ui.components.common.pdf.PdfRendererView
import java.io.File


@Composable
fun PDFViewer(file: String) {
//    renderPdf(File(file))

    val context = LocalContext.current

    val pdfRendererView = remember {
        PdfRendererView(context).apply {
            loadPdfFile(file)
        }
    }


    LazyColumn {
        item {
            AndroidView(
                factory = {
                    pdfRendererView
                }, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(pdfRendererView.getImageAspectRatio())
                    .background(
                        Color.Blue
                    )
            )
        }
    }


    DisposableEffect(Unit) {
        onDispose { pdfRendererView.closePdfRenderer() }
    }

}

private fun renderPdf(file: File) {
    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val pdfRenderer = PdfRenderer(fileDescriptor)

    val pageCount = pdfRenderer.pageCount
    val textRenderer = TextRenderer(TextOutput {

    }, Looper.getMainLooper())

    // Create a list to hold the rendered bitmaps
    val bitmaps = mutableListOf<Bitmap>()

    // Render each page and add the bitmap to the list
    for (pageIndex in 0 until pageCount) {
        val currentPage = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(
            500,
            500,
            Bitmap.Config.ARGB_8888
        ) // Set your desired width and height
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        bitmaps.add(bitmap)
        currentPage.close()
    }

    // Display the bitmaps using a ViewPager or any other suitable UI component

    pdfRenderer.close()
}