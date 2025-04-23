package ru.dimon.ydav2024

import android.database.Cursor
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Запись и чтение информации о батареи устройства
 */
class Battery(database: Database):DbWrite {

    private val _database = database

    private var status:String = ""
    private var level:Float = Float.NaN
    private var temperature:Float = Float.NaN
    private var charge:String = ""

    /**
     * Запись данных в базу данных
     */
    fun write(
        status:String,
        level:Float,
        temperature:Float,
        charge:String
    ){
        val sql = "UPDATE battery SET temperature=$temperature, level=$level, status='$status', charge='$charge' WHERE name='BATTERY'"
        exec(this._database, sql)
    }

    private fun read(){
        val db = this._database.getDatabase()
        val cursor: Cursor =
            db.query("battery", null, "name = ?", arrayOf("BATTERY"), null, null, null)
        val idTemperature = cursor.getColumnIndex("temperature")
        val idLevel = cursor.getColumnIndex("level")
        val idStatus = cursor.getColumnIndex("status")
        val idCharge = cursor.getColumnIndex("charge")
        if (cursor.moveToFirst()) {
            do {
                this.temperature = cursor.getFloat(idTemperature)
                this.level = cursor.getFloat(idLevel)
                this.status = cursor.getString(idStatus)
                this.charge = cursor.getString(idCharge)
            } while (cursor.moveToNext())
        }
        cursor.close()

    }

    fun json():BatteryData{
        this.read()
        return BatteryData(temperature, level, status, charge)
    }
}

@Serializable
data class BatteryData(val temperature: Float, val level: Float, val status: String, val charge: String){}