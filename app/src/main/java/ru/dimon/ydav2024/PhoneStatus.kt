package ru.dimon.ydav2024

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.icu.util.GregorianCalendar
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Запись и чтение информации о звонке
 */
class PhoneStatus(context: Context) {

    private val con=context

    fun write(
        phone:String="",
        status:String=""
    ){
        val time= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val dbHelper = DBHelper(con)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("phone", phone)
        cv.put("status", status)
        cv.put("time", time.format(formatter))
        db.insert("caltel",null, cv)
        dbHelper.close()
    }

    fun json():String{
        var jsonText=""
        val dbHelper = DBHelper(con)
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query("caltel", null, null, null, null, null, null)
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
        dbHelper.close()
        return "["+jsonText.dropLast(1)+"]"
    }
}