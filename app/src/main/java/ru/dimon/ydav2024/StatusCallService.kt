package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder

class StatusCallService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val status = intent.extras!!.getString("Status") as String
        val phoneStatus = PhoneStatus(this@StatusCallService)
        phoneStatus.write(status=status)
        stopSelf()
        return START_NOT_STICKY
    }
}