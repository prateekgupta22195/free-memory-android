package com.pg.cloudcleaner.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.pg.cloudcleaner.BuildConfig
import timber.log.Timber

class Ref(var value: Int)

@Composable
fun LogCompositions(tag: String = COMPOSITION_TAG, msg: String) {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(0) }
        SideEffect { ref.value++ }
        Timber.tag(tag).e("Compositions: $msg ${ref.value}")
    }
}

private const val COMPOSITION_TAG = "Composition log"
