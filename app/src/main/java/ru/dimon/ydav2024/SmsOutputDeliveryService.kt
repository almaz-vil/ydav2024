package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.lang.ref.WeakReference

class SmsOutputDeliveryService: Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val id = intent.extras!!.getString("id")
        val delivery = intent.extras!!.getString("delivery")
        Database.setContext(WeakReference(this@SmsOutputDeliveryService).get()!!)
        val smsOutput = SmsOutput(WeakReference(this@SmsOutputDeliveryService).get()!!, Database)
        smsOutput.writeDelivery(id!!, delivery!!)
        stopSelf()
        return START_NOT_STICKY
    }

}