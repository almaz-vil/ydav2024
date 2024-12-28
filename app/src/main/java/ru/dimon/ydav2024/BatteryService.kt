package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BatteryService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val temperature = intent.extras!!.getFloat("Temper")
        val lavel = intent.extras!!.getFloat("Lavel")
        val maxlavel = intent.extras!!.getFloat("MaxLavel")
        val status = intent.extras!!.getString("Status")
        val battery = Battery(this@BatteryService)
        battery.write(status!!, lavel, maxlavel, temperature)
        stopSelf()
        return START_NOT_STICKY
    }
}