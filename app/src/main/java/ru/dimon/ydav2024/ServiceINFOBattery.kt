package ru.dimon.ydav2024

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.os.IBinder
import android.util.Log


class ServiceINFOBattery : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bat_temper = intent.extras!!.getString("Temper")
        val bat_lavel = intent.extras!!.getString("Lavel")
        val bat_maxlavel = intent.extras!!.getString("MaxLavel")
        val bat_status = intent.extras!!.getString("Status")
        val dbHelper = DBHelper(this@ServiceINFOBattery)
        val db = dbHelper.writableDatabase
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        val cv = ContentValues()
        cv.put("temper", bat_temper)
        cv.put("lavel", bat_lavel)
        cv.put("maxlavel", bat_maxlavel)
        cv.put("status", bat_status)
        // обновляем запись
        db.update("batter", cv, "name='BATTER'", null)
        Log.d("Ydav","обновляем базу БАТАРЕЯ")
        dbHelper.close()
        stopSelf()
        return START_NOT_STICKY
    }
}