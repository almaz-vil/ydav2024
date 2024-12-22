package ru.dimon.ydav2024

import android.app.Service.TELEPHONY_SERVICE
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log


class SignalStrength(context: Context) {
    val _context=context
    var ASU=0
    var RSSI=0
    var ERR=0
    var Nomer=""


    fun runStrength() {
        val listener = object : PhoneStateListener(){
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                val listCellSignal = signalStrength.getCellSignalStrengths();
                val ASU = listCellSignal[0].asuLevel
                val RSSI = listCellSignal[0].level
                val ERR = listCellSignal[0].dbm
                val dbHelper = DBHelper(_context)
                // создаем объект для данных
                val cv = ContentValues()
                // подключаемся к БД
                val db1: SQLiteDatabase = dbHelper.getWritableDatabase()
                // подготовим данные для вставки в виде пар: наименование столбца - значение
                Log.d("год", "база")
                cv.put("rssi", RSSI)
                cv.put("asu", ASU)
                cv.put("error", ERR)
                // вставляем запись и получаем ее ID
                val rowID = db1.update("gsm", cv, "name='RSSI'", null)
                dbHelper.close()
            }

            //входящий звонок
            override fun onCallStateChanged(state: Int, incomingNumer: String) {
                val dbHelper = DBHelper(_context)
                val db: SQLiteDatabase = dbHelper.getWritableDatabase()
                if (state == TelephonyManager.CALL_STATE_RINGING) { //идёт вызов
                    // подготовим данные для вставки в виде пар: наименование столбца - значение
                    val Nomer = incomingNumer
                    val cv = ContentValues()
                    cv.put("nomer", incomingNumer)
                    // вставляем запись и получаем ее ID
                    db.update("caltel", cv, "name='CALTEL'", null)
                }
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) { //идёт разговор
                    // подготовим данные для вставки в виде пар: наименование столбца - значение
                    Nomer = " Разговор с $incomingNumer"
                    val cv = ContentValues()
                    cv.put("nomer", " Разговор с $incomingNumer")
                    // вставляем запись и получаем ее ID
                    db.update("caltel", cv, "name='CALTEL'", null)
                }
                if (state == TelephonyManager.CALL_STATE_IDLE) { //идёт вызов
                    // подготовим данные для вставки в виде пар: наименование столбца - значение
                    Nomer = " отключился "
                    val cv = ContentValues()
                    cv.put("nomer", " отключился ")
                    // вставляем запись и получаем ее ID
                    db.update("caltel", cv, "name='CALTEL'", null)
                }
                dbHelper.close()
            }
        }

        val telephonyManager =_context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS )
    }

    fun readLavel(){

        //из базы об уровне сигнала
        val db1: SQLiteDatabase
        val dbHelper1 = DBHelper(_context)
        db1 = dbHelper1.readableDatabase
        val selection = "name = ?"
        val selectionArgs = arrayOf("RSSI")
        var cursor = db1.query("gsm", null, selection, selectionArgs, null, null, null)
        RSSI = cursor.getInt(2)
        ASU = cursor.getInt(3)
        ERR = cursor.getInt(4)
        cursor!!.close()

        // из базы об состотоянии входящем телефонном вызове
        val selection1 = "name = ?"
        val selectionArgs1 = arrayOf("CALTEL")
        cursor = db1.query("caltel", null, selection1, selectionArgs1, null, null, null)
        Nomer = cursor.getString(2)
        cursor!!.close()
        dbHelper1.close()
    }
}