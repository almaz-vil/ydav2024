package ru.dimon.ydav2024

import android.database.Cursor
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SmsInput(database: Database):DbWrite {
    private val table = "insms"
    private val _database=database

    fun delete(where :String){
        if (where.length>0) {
            exec(_database, "DELETE FROM $table WHERE $where")
        }
    }
    fun count():Int{
        return count(_database,"SELECT count(*) as count FROM $table")
    }

    fun write(
        date: Long,
        phone:String="",
        body:String=""
    ){
        val sdf = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault())
        val formatTime = sdf.format(Date(date))
        val sql = "INSERT INTO $table(phone, body, time) VALUES ('$phone', '$body', '$formatTime')"
        exec(_database, sql)
    }

    fun json():String{
        var jsonText=""
        val db = _database.getDatabase()
        val cursor: Cursor = db.query(table, null, null, null, null, null, "_id DESC")
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