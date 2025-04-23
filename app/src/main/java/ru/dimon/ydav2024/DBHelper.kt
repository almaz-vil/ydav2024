package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.ref.WeakReference

class DBHelper(context: Context):
    SQLiteOpenHelper(WeakReference(context).get(), DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "myDB"
    }

    override
    fun onCreate(sQLiteDatabase:SQLiteDatabase?) {
        sQLiteDatabase?.execSQL("create table caltel (_id integer primary key autoincrement, phone string, status string, time string);")
        sQLiteDatabase?.execSQL("create table insms (_id integer primary key autoincrement, phone string, body string, time string);")
        sQLiteDatabase?.execSQL("create table outputSms (_id integer primary key autoincrement, phone string, text string, id string, sent string, sent_time string, delivery string, delivery_time string);")
        sQLiteDatabase?.execSQL("create table battery (_id integer primary key autoincrement, name string, temperature string, level string, status string, charge string);")
        sQLiteDatabase?.execSQL("insert into battery (name, temperature, level, status, charge) values ('BATTERY','-1','-1','-1', '')")
        sQLiteDatabase?.execSQL("create table infolte (_id integer primary key autoincrement, name string, sim_county_iso string, sim_operator_name string, sim_operator string, network_type string, param_signal string);")
        sQLiteDatabase?.execSQL("insert into infolte (name, sim_county_iso, sim_operator_name, sim_operator, network_type, param_signal) values ('INFOLTE','','','','','')")
        sQLiteDatabase?.execSQL("create table ussd (_id integer primary key autoincrement, name string, failure_code string, response string);")
        sQLiteDatabase?.execSQL("insert into ussd (name, failure_code, response) values ('USSD','','')")
    }

    override
    fun onUpgrade(sQLiteDatabase: SQLiteDatabase?, oldVersoin: Int, newVersion: Int) {
    }

}
