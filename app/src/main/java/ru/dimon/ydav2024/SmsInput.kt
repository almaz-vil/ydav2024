package ru.dimon.ydav2024

import android.database.Cursor
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SmsInput(database: Database):DbWrite {
    private val table = "insms"
    private val _database=database

    fun delete(where :String){
        val sql = "DELETE FROM $table WHERE $where"
        Log.d("Ydav", "$sql ")
    //    exec(_database, sql)
    }
    fun count():Int{
        return count(_database,"SELECT count(*) as count FROM $table")
    }

    fun write(
        date: Long,
        phone:String="",
        body:String=""
    ){
        val time= LocalDateTime.ofEpochSecond(date,0, ZoneOffset.ofHours(+3))
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val formatTime = time.format(formatter)
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