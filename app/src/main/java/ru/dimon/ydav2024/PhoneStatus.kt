package ru.dimon.ydav2024

import android.content.Context
import android.database.Cursor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Запись и чтение информации о звонке
 */
class PhoneStatus(context: Context):DbWrite {

    private val _context=context

    fun write(
        phone:String="",
        status:String=""
    ){
        val time= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val format_time = time.format(formatter)
        val sql = "INSERT INTO caltel(phone, status, time) VALUES ('$phone', '$status', '$format_time')"
        exec(_context, sql)
    }

    fun json():String{
        var jsonText=""
        val dbHelper = DBHelper(_context)
        val db = dbHelper.readableDatabase
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
        db.close()
        dbHelper.close()
        return "["+jsonText.dropLast(1)+"]"
    }
}