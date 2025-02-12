package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.lang.ref.WeakReference

class SmsOutputDeliveryBroadcastReceiver: BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action==context.getString(R.string.delivery_sms_action)){
            val id = intent.getStringExtra("id")
            val smsOutputSentService = Intent(
                WeakReference(context).get(),
                SmsIInputService::class.java)
            smsOutputSentService.putExtra("id", id)
            smsOutputSentService.putExtra("delivery", resultCode)
            context.startService(smsOutputSentService)

        }
    }
}