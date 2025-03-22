package ru.dimon.ydav2024

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter


class ApplicationYdav: Application() {

    override fun onCreate() {
        super.onCreate()
        //Создания канала уведомлений
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
         //Для учёта изменений состояния телефона
        val statusCallBroadcastReceiver = StatusCallBroadcastReceiver()
        registerReceiver(statusCallBroadcastReceiver, IntentFilter("android.intent.action.PHONE_STATE"))
        //Для учёта изменений состояния входящих СМС
        val smsInputBroadcastReceiver = SmsInputBroadcastReceiver()
        registerReceiver(smsInputBroadcastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        //Для учёта изменений состояния WIFI
        val wifiBroadcastReceiver = WifiBroadcastReceiver()
        registerReceiver(wifiBroadcastReceiver, IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"))
        //СМС
        val smsOutputSentBroadcastReceiver = SmsOutputSentBroadcastReceiver()
        registerReceiver(smsOutputSentBroadcastReceiver, IntentFilter("ru.dimon.ydav2024.SENT_SMS_ACTION"), RECEIVER_NOT_EXPORTED)
        val smsOutputDeliveryBroadcastReceiver = SmsInputBroadcastReceiver()
        registerReceiver(smsOutputDeliveryBroadcastReceiver, IntentFilter("ru.dimon.ydav2024.DELIVERY_SMS_ACTION"), RECEIVER_NOT_EXPORTED)

    }
}