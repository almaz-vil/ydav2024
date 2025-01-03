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
    private var lavel:Float = Float.NaN
    private var maxlavel:Float = Float.NaN
    private var temperature:Float = Float.NaN

    /**
     * Запись данных в базу данных
     */
    fun write(
        status:String,
        lavel:Float,
        maxlavel:Float,
        temperature:Float
    ){
        val dbHelper = DBHelper(con)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("temper", temperature)
        cv.put("lavel", lavel)
        cv.put("maxlavel", maxlavel)
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
        val idLavel =cursor.getColumnIndex("lavel")
        val idMaxlavel =cursor.getColumnIndex("maxlavel")
        val idStatus =cursor.getColumnIndex("status")
        if(cursor.moveToFirst()) {
            do {
                this.temperature = cursor.getFloat(idTemperature)
                this.lavel = cursor.getFloat(idLavel)
                this.maxlavel = cursor.getFloat(idMaxlavel)
                this.status = cursor.getString(idStatus)
            } while(cursor.moveToNext())
        }
        cursor.close()
        dbHelper.close()
    }

    fun json():String{
        this.read()
        return """{"tempetapure":${this.temperature},
                "lavel":${this.lavel},
                "status":"${this.status}"}"""
    }
}