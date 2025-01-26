package ru.dimon.ydav2024

import android.content.Context
import android.database.Cursor
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Запись и чтение информации о звонке
 */
class PhoneStatus(database: Database):DbWrite {

    private val _database=database

    fun write(
        phone:String="",
        status:String=""
    ){
        val time= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val format_time = time.format(formatter)
        val sql = "INSERT INTO caltel(phone, status, time) VALUES ('$phone', '$status', '$format_time')"
        exec(this._database, sql)

    }

    fun json():String{
        var jsonText=""
        val db = this._database.getDatabase()
        val cursor: Cursor = db.query("caltel", null, null, null, null, null, "_id DESC")
        val idTime =cursor.getColumnIndex("time")
        val idPhone =cursor.getColumnIndex("phone")
        val idStatus =cursor.getColumnIndex("status")
        if (cursor.moveToFirst()){
            do {
                val time = cursor.getString(idTime)
                val phone = cursor.getString(idPhone)
                val status =cursor.getString(idStatus)
                jsonText+="""{"time":"$time",
                "phone":"$phone",
                "status":"$status"},"""
            } while (cursor.moveToNext())
        }
        cursor.close()
        return "["+jsonText.dropLast(1)+"]"
    }
}