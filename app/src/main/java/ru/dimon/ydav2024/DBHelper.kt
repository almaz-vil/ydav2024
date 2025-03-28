package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.ref.WeakReference

class DBHelper(context: Context):
    SQLiteOpenHelper(WeakReference(context).get(), DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "myDB"
    }

    override
    fun onCreate(sQLiteDatabase:SQLiteDatabase?) {
        sQLiteDatabase?.execSQL("create table caltel (_id integer primary key autoincrement, phone string, status string, time string);")
        sQLiteDatabase?.execSQL("create table insms (_id integer primary key autoincrement, phone string, body string, time string);")
        sQLiteDatabase?.execSQL("create table outputSms (_id integer primary key autoincrement, phone string, text string, id string, sent string, sent_time string, delivery string, delivery_time string);")
        sQLiteDatabase?.execSQL("create table battery (_id integer primary key autoincrement, name string, temperature string, level string, status string, charge string);")
        sQLiteDatabase?.execSQL("insert into battery (name, temperature, level, status, charge) values ('BATTERY','-1','-1','-1', '')")
        sQLiteDatabase?.execSQL("create table infolte (_id integer primary key autoincrement, name string, RSSI integer, RSRP integer, RSSNR integer, RSRQ integer);")
        sQLiteDatabase?.execSQL("insert into infolte (name, RSSI, RSRP, RSSNR, RSRQ) values ('INFOLTE',0,0,0,0)")
       // sQLiteDatabase?.execSQL("create table address (_id integer primary key autoincrement, name string, ip string);")
       // sQLiteDatabase?.execSQL("insert into address (name, ip) values ('server','')")
    }

    override
    fun onUpgrade(sQLiteDatabase: SQLiteDatabase?, oldVersoin: Int, newVersion: Int) {
    }

}
