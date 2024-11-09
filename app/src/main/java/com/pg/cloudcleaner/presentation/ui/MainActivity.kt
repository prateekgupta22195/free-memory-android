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
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.text
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.neatroots.fbcrashlogs_testapp.LoggingUtils
import com.pg.cloudcleaner.BuildConfig
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.app.CloudCleanerApp
import com.pg.cloudcleaner.helper.ReadFileWorker
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

// Use LoggingUtils to log initialization to both Firebase and Sentry
        LoggingUtils.logInitialization("MainActivity initialized successfully")
        Timber.d("manual log successfully created")

        Sentry.init { options ->
            options.dsn =
                "https://a7a1e5324f974f6a9bd88b1fb0b59302@o4505045496037376.ingest.sentry.io/4505045496954880"
            options.environment = "production" // Or your desired environment
        }

        LoggingUtils.logManualMessage("MainActivity initialized successfully")
        setContent {
            ForceCrashButton()
        }

        val crashButton = Button(this).apply {
            text = "Test Crash"
            setOnClickListener {
                Toast.makeText(context, "Crash log recorded, app will crash", Toast.LENGTH_SHORT).show()

                LoggingUtils.logCrash("Force Crash triggered by user")
                // Force a crash
                throw RuntimeException("This is a forced crash!")

            }
        }

        val crashButton2 = Button(this).apply {
            text = "Exception log test"
            setOnClickListener {
                try {
                    val result = 10 / 0 // This will throw an ArithmeticException
                } catch (e: Exception) {
                    LoggingUtils.logException(e)
                    Toast.makeText(context, "Exception log recorded", Toast.LENGTH_SHORT).show()
                }
            }
        }

// Create a FrameLayout to center the button
        val frameLayout = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

// Add the button to the FrameLayout
        frameLayout.addView(crashButton, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.START
        ))

        frameLayout.addView(crashButton2, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.END
        ))

// Add the FrameLayout to the activity's content view
        addContentView(frameLayout, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

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

    @Composable
    private fun ForceCrashButton() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    // Log the crash details before forcing the crash
                    LoggingUtils.logCrash("Force Crash triggered by user")

                    // Force a crash
                    throw RuntimeException("This is a forced crash!")
                }
            )

            {
                Text(text = "Force Crash")
            }
        }
    }

    @Preview(showBackground = true)

    @Composable
    fun DefaultPreview() {
        ForceCrashButton()
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


