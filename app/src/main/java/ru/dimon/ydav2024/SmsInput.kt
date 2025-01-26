package ru.dimon.ydav2024

import android.content.Context
import android.database.Cursor
import android.util.Log
import java.lang.ref.WeakReference
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SmsInput(database: Database):DbWrite {

    private val _database=database

    fun write(
        date: Long,
        phone:String="",
        body:String=""
    ){
        val time= LocalDateTime.ofEpochSecond(date,0, ZoneOffset.ofHours(+3))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val formatTime = time.format(formatter)
        val sql = "INSERT INTO insms(phone, body, time) VALUES ('$phone', '$body', '$formatTime')"
        Log.d("Ydav", "$sql ")
        exec(_database, sql)
    }

    fun json():String{
        var jsonText=""
        val db = _database.getDatabase()
        val cursor: Cursor = db.query("insms", null, null, null, null, null, "_id DESC")
        val idId =cursor.getColumnIndex("_id")
        val idTime =cursor.getColumnIndex("time")
        val idPhone =cursor.getColumnIndex("phone")
        val idBody =cursor.getColumnIndex("body")
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(idId)
                val time = cursor.getString(idTime)
                val phone = cursor.getString(idPhone)
                val body =cursor.getString(idBody)
                jsonText+="""{"id":"$id",
                "time":"$time",
                "phone":"$phone",
                "body":"$body"},"""
            } while (cursor.moveToNext())
        }
        cursor.close()
        return "["+jsonText.dropLast(1)+"]"
    }

}