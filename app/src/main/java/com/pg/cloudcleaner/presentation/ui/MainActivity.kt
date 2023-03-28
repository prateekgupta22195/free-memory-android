package com.pg.cloudcleaner.presentation.ui

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.pg.cloudcleaner.BuildConfig
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.CloudCleanerApp
import com.pg.cloudcleaner.helper.ReadFileWorker
import timber.log.Timber


@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Permission granted")
                onStoragePermissionGranted()
            }
        }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isStoragePermissionGranted()) {
            Timber.d("Permission not granted")
            seekStoragePermission()
        } else {
            Timber.d("Permission already granted")
            onStoragePermissionGranted()
        }

    }

    private fun isStoragePermissionGranted(): Boolean {
        Timber.d("checking permission")
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ActivityCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun seekStoragePermission() {
        Timber.d("Seeking Permission")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Permission needed!",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Settings") {

                    val intent: Intent = try {
                        val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            uri
                        )
                    } catch (ex: Exception) {
                        Intent().apply {
                            action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        }
                    }
                    resultLauncher.launch(intent)
                }
                .show()
        } else {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 123)
            }
        }

    }


    private fun onStoragePermissionGranted() {
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "file reader",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ReadFileWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("abc")
                .build(),
        )

        setContent {
            App.instance.initNavController(rememberNavController())
            CloudCleanerApp()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 123 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Timber.d("Permission granted")
            onStoragePermissionGranted()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
