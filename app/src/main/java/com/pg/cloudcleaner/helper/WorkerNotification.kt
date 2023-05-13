package com.pg.cloudcleaner.helper

import android.app.Notification

interface WorkerNotification {
    fun createChannel()
    fun getNotification() : Notification
    fun setForegroundNotification(): Notification
}