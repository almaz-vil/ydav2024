package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SmsIInputService: Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val phone = intent.extras!!.getString("phone")
        val date = intent.extras!!.getLong("date")
        val body = intent.extras!!.getString("body")
        Log.d("Ydav", "onStartCommand: $phone $date $body")
        val smsInput = SmsInput(this@SmsIInputService)
        smsInput.write(date, phone!!, body!!)
        stopSelf()
        return START_NOT_STICKY
    }
}