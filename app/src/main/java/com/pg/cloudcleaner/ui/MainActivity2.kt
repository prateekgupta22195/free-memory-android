package com.pg.cloudcleaner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pg.cloudcleaner.R
import timber.log.Timber

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Timber.e("onCreate")
    }

    override fun onRestart() {
        Timber.e("onRestart")
        super.onRestart()
    }

    override fun onResume() {
        Timber.e("onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.e("onPause")
        super.onPause()
    }

    override fun onStart() {
        Timber.e("onStart")
        super.onStart()
    }

    override fun onStop() {
        Timber.e("onStop")
        super.onStop()
    }
}
