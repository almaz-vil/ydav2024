package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class ReadCallBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state=intent.getStringExtra(TelephonyManager.EXTRA_STATE) as String
        Log.d("Ydav ", "$state cтатус")
    }
}