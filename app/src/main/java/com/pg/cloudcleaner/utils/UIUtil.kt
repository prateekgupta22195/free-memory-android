package com.pg.cloudcleaner.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) = with(this) {
    val context = this
    withContext(Dispatchers.Main) {
        Toast.makeText(context, message, duration).show()
    }
}