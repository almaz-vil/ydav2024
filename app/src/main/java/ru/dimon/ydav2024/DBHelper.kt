package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "myDB"
    }

    override
    fun onCreate(sQLiteDatabase:SQLiteDatabase?) {
        sQLiteDatabase?.execSQL("create table caltel (_id integer primary key autoincrement, name string, nomer string);")
        sQLiteDatabase?.execSQL("insert into caltel (name, nomer) values ('CALTEL','нет')")
        sQLiteDatabase?.execSQL("create table insms (_id integer primary key autoincrement, nomerfona string, textmsg string, dattime string);")
        sQLiteDatabase?.execSQL("create table outsms (_id integer primary key autoincrement, nomerfona string, textmsg string, nomerspk string, error string, result string);")
        sQLiteDatabase?.execSQL("create table batter (_id integer primary key autoincrement, name string, temper string, lavel string, maxlavel string, status string);")
        sQLiteDatabase?.execSQL("insert into batter (name, temper, lavel, maxlavel, status) values ('BATTER','-1','-1','-1','-1')")
        sQLiteDatabase?.execSQL("create table infolte (_id integer primary key autoincrement, name string, RSSI integer, RSRP integer, RSSNR integer, RSRQ integer);")
        sQLiteDatabase?.execSQL("insert into infolte (name, RSSI, RSRP, RSSNR, RSRQ) values ('INFOLTE',-1,-1,-1,-1)")
    }

    override
    fun onUpgrade(sQLiteDatabase: SQLiteDatabase?, oldVersoin: Int, newVersion: Int) {
    }

}
