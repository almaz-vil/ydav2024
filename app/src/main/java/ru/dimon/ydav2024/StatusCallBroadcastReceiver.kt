package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import java.lang.ref.WeakReference

class StatusCallBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state=intent.getStringExtra(TelephonyManager.EXTRA_STATE) as String
        val statusCallService = Intent(WeakReference(context).get(), StatusCallService::class.java)
        statusCallService.putExtra("Status", state)
        context.startService(statusCallService)
    }
}