package ru.dimon.ydav2024

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

/**
 * Запись и чтение информации о батареи устройства
 */
class Battery(context: Context) {

    private val con=context

    private var status:String = ""
    private var level:Float = Float.NaN
    private var max_level:Float = Float.NaN
    private var temperature:Float = Float.NaN

    /**
     * Запись данных в базу данных
     */
    fun write(
        status:String,
        level:Float,
        maxLevel:Float,
        temperature:Float
    ){
        val dbHelper = DBHelper(con)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("temper", temperature)
        cv.put("lavel", level)
        cv.put("maxlavel", maxLevel)
        cv.put("status", status)
        db.update("batter", cv, "name='BATTER'", null)
        Log.d("Ydav","обновляем базу БАТАРЕЯ")
        dbHelper.close()
    }

   private fun read(){
        val dbHelper = DBHelper(con)
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("batter", null, "name = ?", arrayOf("BATTER"), null, null, null)
        val idTemperature =cursor.getColumnIndex("temper")
        val idLevel =cursor.getColumnIndex("lavel")
        val idMaxLevel =cursor.getColumnIndex("maxlavel")
        val idStatus =cursor.getColumnIndex("status")
        if(cursor.moveToFirst()) {
            do {
                this.temperature = cursor.getFloat(idTemperature)
                this.level = cursor.getFloat(idLevel)
                this.max_level = cursor.getFloat(idMaxLevel)
                this.status = cursor.getString(idStatus)
            } while(cursor.moveToNext())
        }
        cursor.close()
        dbHelper.close()
    }

    fun json():String{
        this.read()
        return """{"temperature":${this.temperature},
                "level":${this.level},
                "status":"${this.status}"}"""
    }
}