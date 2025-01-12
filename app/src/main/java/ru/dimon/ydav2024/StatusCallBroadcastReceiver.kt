package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class StatusCallBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state=intent.getStringExtra(TelephonyManager.EXTRA_STATE) as String
        val statusCallService = Intent( context, StatusCallService::class.java)
        statusCallService.putExtra("Status", state)
        context.startService(statusCallService)
    }
}