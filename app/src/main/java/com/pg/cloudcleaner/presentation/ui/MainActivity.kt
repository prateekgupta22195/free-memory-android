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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pg.cloudcleaner.BuildConfig
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.CloudCleanerApp
import com.pg.cloudcleaner.helper.ReadFileWorker
import com.pg.cloudcleaner.helper.UpdateChecksumWorker
import com.pg.cloudcleaner.presentation.ui.pages.PermissionRequiredComposable
import java.util.concurrent.TimeUnit


@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>

    private val hasStoragePermissionState = mutableStateOf(false)


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        hasStoragePermissionState.value = isStoragePermissionGranted()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
//             TODO: change this later and check why callback not working properly
            hasStoragePermissionState.value = isStoragePermissionGranted()

            if (hasStoragePermissionState.value) {
                // Permission granted
                onStoragePermissionGranted()
            }
        }

        setContent {
            val hasStoragePermission by hasStoragePermissionState

            LaunchedEffect(hasStoragePermission) {
                if (hasStoragePermission) {
                    onStoragePermissionGranted()
                }
            }

            if (!hasStoragePermission) {
                PermissionRequiredComposable(
                    onRequestPermission = { seekStoragePermission() },
                    onRefreshPermissionState = {
                        hasStoragePermissionState.value = isStoragePermissionGranted()
                    }
                )
            } else {
                App.instance.initNavController(rememberNavController())
                CloudCleanerApp()
            }
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
            val intent: Intent = try {
                val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            } catch (ex: Exception) {
                Intent().apply {
                    action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                }
            }
            requestPermissionLauncher.launch(intent)
        } else {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 123)
            }
        }

    }


    private fun onStoragePermissionGranted() {
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniqueWork(
            "file reader",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ReadFileWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("abc").build(),
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 123 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            hasStoragePermissionState.value = true
            onStoragePermissionGranted()
        } else {
            hasStoragePermissionState.value = isStoragePermissionGranted()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        hasStoragePermissionState.value = isStoragePermissionGranted()
    }

}
