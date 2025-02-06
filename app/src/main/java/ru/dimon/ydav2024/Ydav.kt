package ru.dimon.ydav2024

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class Ydav: Application() {
    override fun onCreate() {
        super.onCreate()
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)
        val channelDescription = getString(R.string.channel_description)
        val notificationChannel = NotificationChannel(
            channelId, channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        notificationChannel.description = channelDescription
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

    }
}