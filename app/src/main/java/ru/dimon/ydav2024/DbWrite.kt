package ru.dimon.ydav2024

import android.telephony.TelephonyManager
import java.util.Formatter
import java.util.GregorianCalendar

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

    fun timeNow(format: String=""):String{
        val gregorianCalendar = GregorianCalendar()
        val formatter = Formatter()
        val t=gregorianCalendar.timeInMillis;
        val y= formatter.format("%tF %tT ",t, t)
        return "$y"
    }

     fun networkTypetoString(network: Int):String {
        return when (network) {
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
            TelephonyManager.NETWORK_TYPE_GSM -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVD0_0"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVD0_A"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO_B"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "EHRPO"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPAP"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_IWLAN -> "4G"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            else -> "?$network"
        }
    }


}