package com.pg.cloudcleaner.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.pg.cloudcleaner.app.AppData
import com.pg.cloudcleaner.app.CloudCleanerApp
import com.pg.cloudcleaner.helper.ReadFileWorker

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "file reader",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ReadFileWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build(),
        )

        setContent {
            AppData.instance().initNavController(rememberNavController())
            CloudCleanerApp()
        }
    }
}
