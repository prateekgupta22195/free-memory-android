package com.pg.cloudcleaner.utils

import android.content.Context
import com.pg.cloudcleaner.app.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SavedMemoryTracker {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_SAVED_BYTES = "total_saved_bytes"

    private val _totalSavedBytes = MutableStateFlow(0L)
    val totalSavedBytes: StateFlow<Long> = _totalSavedBytes

    fun initialize() {
        _totalSavedBytes.value = prefs().getLong(KEY_SAVED_BYTES, 0L)
    }

    fun addSavedBytes(bytes: Long) {
        if (bytes <= 0L) return
        val newTotal = prefs().getLong(KEY_SAVED_BYTES, 0L) + bytes
        prefs().edit().putLong(KEY_SAVED_BYTES, newTotal).apply()
        _totalSavedBytes.value = newTotal
    }

    private fun prefs() =
        App.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
