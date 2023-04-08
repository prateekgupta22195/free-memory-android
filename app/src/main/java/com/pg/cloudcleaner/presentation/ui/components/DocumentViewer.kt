package com.pg.cloudcleaner.presentation.ui.components


import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DocumentViewer(url: String) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    // Handle progress changes
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    // Handle received title
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    // Handle showing custom view (e.g. for full screen video)
                }

                override fun onHideCustomView() {
                    // Handle hiding custom view
                }
            }
        }
    }

    LaunchedEffect(url) {
        webView.loadUrl("file://$url")
    }

    AndroidView(
        factory = { webView },
        update = {
            // Update the WebView as needed
        }
    )
}