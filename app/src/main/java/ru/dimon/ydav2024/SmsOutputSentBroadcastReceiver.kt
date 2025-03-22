package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.lang.ref.WeakReference

class SmsOutputSentBroadcastReceiver: BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action==context.getString(R.string.sent_sms_action)){
            val id = intent.getStringExtra("id")
            val smsOutputSentService = Intent(
                WeakReference(context).get(),
                SmsOutputSentService::class.java)
            smsOutputSentService.putExtra("id", id)
            smsOutputSentService.putExtra("sent", resultCode)
            Log.d("Ydav", "BR id=$id sent=${resultCode} ")
            context.startService(smsOutputSentService)

        }
    }
}