package ru.dimon.ydav2024

import android.database.Cursor

/**
 * Запись и чтение информации о батареи устройства
 */
class Battery(database: Database):DbWrite {

    private val _database = database

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
        val sql = "UPDATE batter SET temper=$temperature, lavel=$level, maxlavel=$maxLevel, status='$status' WHERE name='BATTER'"
        exec(this._database, sql)
    }

    private fun read(){
        val db = this._database.getDatabase()
        val cursor: Cursor =
            db.query("batter", null, "name = ?", arrayOf("BATTER"), null, null, null)
        val idTemperature = cursor.getColumnIndex("temper")
        val idLevel = cursor.getColumnIndex("lavel")
        val idMaxLevel = cursor.getColumnIndex("maxlavel")
        val idStatus = cursor.getColumnIndex("status")
        if (cursor.moveToFirst()) {
            do {
                this.temperature = cursor.getFloat(idTemperature)
                this.level = cursor.getFloat(idLevel)
                this.max_level = cursor.getFloat(idMaxLevel)
                this.status = cursor.getString(idStatus)
            } while (cursor.moveToNext())
        }
        cursor.close()

    }

    fun json():String{
        this.read()
        return """{"temperature":${this.temperature},
                "level":${this.level},
                "status":"${this.status}"}"""
    }

}