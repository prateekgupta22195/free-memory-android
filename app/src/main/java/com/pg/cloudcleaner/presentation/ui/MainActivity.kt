package com.pg.cloudcleaner.presentation.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.app.ActivityCompat
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


@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CloudCleaner)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
//             TODO: change this later and check why callback not working properly
            if (isStoragePermissionGranted()) {
                // Permission granted
                onStoragePermissionGranted()
            } else {
                // Permission denied
                seekStoragePermission()
            }
        }


        if (!isStoragePermissionGranted()) {
            seekStoragePermission()
        } else {
            onStoragePermissionGranted()
        }

    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ActivityCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 = ActivityCompat.checkSelfPermission(
                this@MainActivity, WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun seekStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Allow Free Memory to access files",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                val intent: Intent = try {
                    val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri
                    )
                } catch (ex: Exception) {
                    Intent().apply {
                        action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    }
                }
                requestPermissionLauncher.launch(intent)
            }.show()
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
            OneTimeWorkRequestBuilder<ReadFileWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("abc").build(),
        )

        setContent {
            App.instance.initNavController(rememberNavController())
            CloudCleanerApp()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 123 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onStoragePermissionGranted()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}


