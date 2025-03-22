package ru.dimon.ydav2024

import android.database.Cursor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Запись и чтение информации о звонке
 */
class PhoneStatus(database: Database):DbWrite {
    private val table = "caltel"
    private val _database=database

    fun delete(where :String){
        if (where.isNotEmpty()) {
            exec(_database, "DELETE FROM $table WHERE $where")
        }
    }

    fun count():Int{
        return count(_database,"SELECT count(*) as count FROM $table")
    }
    fun write(
        phone:String="",
        status:String=""
    ){
        val sql = "INSERT INTO $table(phone, status, time) VALUES ('$phone', '$status', '${timeNow()}')"
        exec(this._database, sql)

    }

    fun json():String{
        var jsonText=""
        val db = this._database.getDatabase()
        val cursor: Cursor = db.query(table, null, null, null, null, null, "_id DESC")
        val idId =cursor.getColumnIndex("_id")
        val idTime =cursor.getColumnIndex("time")
        val idPhone =cursor.getColumnIndex("phone")
        val idStatus =cursor.getColumnIndex("status")
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(idId)
                val time = cursor.getString(idTime)
                val phone = cursor.getString(idPhone)
                val status =cursor.getString(idStatus)
                jsonText+="""{"id":"$id",
                 "time":"$time",
                "phone":"$phone",
                "status":"$status"},"""
            } while (cursor.moveToNext())
        }
        cursor.close()
        return "["+jsonText.dropLast(1)+"]"
    }
}