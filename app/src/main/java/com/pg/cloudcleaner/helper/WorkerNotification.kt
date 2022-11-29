package com.pg.cloudcleaner.helper

import android.app.Notification

interface WorkerNotification {
    fun getNotification() : Notification
}