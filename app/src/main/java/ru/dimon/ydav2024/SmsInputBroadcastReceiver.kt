package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SmsInputBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages =  Telephony.Sms.Intents.getMessagesFromIntent(intent)
        var phone=""
        var date=0L
        var body=""
        for (message  in messages) {
                phone = message.originatingAddress.toString()
                date = message.timestampMillis
                body += message.messageBody
        }
        val smsIInputService = Intent(WeakReference(context).get(), SmsIInputService::class.java)
        smsIInputService.putExtra("phone", phone)
        smsIInputService.putExtra("date", date)
        smsIInputService.putExtra("body", body)
        context.startService(smsIInputService)
    }
}