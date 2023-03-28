package com.pg.cloudcleaner.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.pg.cloudcleaner.R
import com.pg.cloudcleaner.app.App
import com.pg.cloudcleaner.data.repository.LocalFilesRepoImpl
import com.pg.cloudcleaner.domain.interactors.FileActionInteractor
import com.pg.cloudcleaner.domain.interactors.FileActionInteractorImpl
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.Timer


class ReadFileWorker(context: Context, workerParameters: WorkerParameters) :

    CoroutineWorker(context, workerParameters), WorkerNotification {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "11"
        private const val NOTIFICATION_CHANNEL_NAME = "Work Service"
    }

    override suspend fun doWork(): Result {
         val  startTime  = System.currentTimeMillis();
        val fileInteractor: FileActionInteractor =
            FileActionInteractorImpl(LocalFilesRepoImpl(App.instance.db.localFilesDao()))


        fileInteractor.syncAllFilesToDb(Environment.getExternalStorageDirectory().absolutePath)
        Timber.d("Time for filling DB ${System.currentTimeMillis() - startTime}" )
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            System.currentTimeMillis().toInt(),
            setForegroundNotification()
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannel() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }


    override fun getNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setAutoCancel(true)
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setLocalOnly(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentText("Updating widget")
            .build()
    }

    override fun setForegroundNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return getNotification()
    }

}
