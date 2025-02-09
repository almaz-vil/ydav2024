package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.lang.ref.WeakReference

class BatteryService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val temperature = intent.extras!!.getFloat("Temperature")
        val level = intent.extras!!.getFloat("Level")
        val status = intent.extras!!.getString("Status")
        val charge = intent.extras!!.getString("Charge")
        Database.setContext(WeakReference( this@BatteryService.applicationContext).get()!!)
        val battery = Battery(Database)
        battery.write(status=status!!,
            level=level,
            temperature=temperature,
            charge=charge!!)
        stopSelf()
        return START_NOT_STICKY
    }

}