package ru.dimon.ydav2024

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

interface DbWrite {
    fun exec(database: Database, sql: String){
        val db = database.getDatabase()
        db.beginTransactionNonExclusive()
        try {
            db.execSQL(sql)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }

    fun count(database: Database, sql: String):Int{
        val db = database.getDatabase()
        val cursor = db.rawQuery(sql,null)
        var res = 0
        val idCount = cursor.getColumnIndex("count")
        if (cursor.moveToNext()){
            res = cursor.getInt(idCount)
        }
        cursor.close()
        return res
    }

    fun timeNow(pattern: String = "HH:mm:ss  dd-MM-yyyy"):String{
        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return time.format(formatter)
    }
}