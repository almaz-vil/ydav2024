package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase

interface DbWrite {
    fun exec(_context: Context, sql: String){
        val dbHelper = DBHelper(_context)
        val db: SQLiteDatabase = dbHelper.writableDatabase
        db.beginTransactionNonExclusive()
        try {
            db.execSQL(sql)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
            db.close()
        }
        dbHelper.close()
    }
}