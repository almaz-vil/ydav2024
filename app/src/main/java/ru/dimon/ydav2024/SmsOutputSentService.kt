package ru.dimon.ydav2024

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.lang.ref.WeakReference

class SmsOutputSentService: Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val id = intent.extras!!.getString("id")
        val sent = intent.extras!!.getString("sent")
        Database.setContext(WeakReference(this@SmsOutputSentService).get()!!)
        val smsOutput = SmsOutput(WeakReference(this@SmsOutputSentService).get()!!,Database)
        smsOutput.writeSent(id!!, sent!!)
        stopSelf()
        return START_NOT_STICKY
    }

}