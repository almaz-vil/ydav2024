package ru.dimon.ydav2024

import android.Manifest
import android.app.Service.TELEPHONY_SERVICE
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.checkSelfPermission
import java.util.concurrent.Executor

class MyCellInfoLte(context: Context, mainExecute: Executor) {
    private val _context=context
    private val _mainExecute=mainExecute
    private var pRSSI=-1
    private var pRSRP=-1
    private var pRSSNR=-1
    private var pRSRQ=-1
    private var simCountyIso=""
    private var simOperatorName=""
    private var simOperator=""
    private var networkType=""

    fun run() {
        checkSelfPermission(_context,Manifest.permission.ACCESS_FINE_LOCATION)
        val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        this.simCountyIso = telephonyManager.simCountryIso
        this.simOperatorName = telephonyManager.simOperatorName
        this.simOperator = telephonyManager.simOperator
        this.networkType = networkTypetoString(telephonyManager.dataNetworkType)
        telephonyManager.requestCellInfoUpdate( _mainExecute, object : TelephonyManager.CellInfoCallback() {
            override fun onCellInfo(activeCellInfo: MutableList<CellInfo>) {
                for (cellInfo in activeCellInfo) {
                    val cellInfoLte  = cellInfo as CellInfoLte
                    val vRSRP=cellInfoLte.cellSignalStrength.rsrp
                    val vRSSI=cellInfoLte.cellSignalStrength.rssi
                    val vRSSNR=cellInfoLte.cellSignalStrength.rssnr
                    val vRSRQ=cellInfoLte.cellSignalStrength.rsrq
                    val dbHelper = DBHelper(_context)
                    val cv = ContentValues()
                    val db: SQLiteDatabase = dbHelper.writableDatabase
                    cv.put("RSRP", vRSRP)
                    cv.put("RSSI", vRSSI)
                    cv.put("RSSNR", vRSSNR)
                    cv.put("RSRQ", vRSRQ)
                    db.update("infolte", cv, "name='INFOLTE'", null)
                    dbHelper.close()
                }
            }

        })

    }
    private fun read(){
        val db: SQLiteDatabase
        val dbHelper = DBHelper(_context)
        db = dbHelper.readableDatabase
        val selection = "name = ?"
        val selectionArgs = arrayOf("INFOLTE")
        val cursor = db.query("infolte", null, selection, selectionArgs, null, null, null)
        val idRSRP =cursor.getColumnIndex("RSRP")
        val idRSSI =cursor.getColumnIndex("RSSI")
        val idRSSNR =cursor.getColumnIndex("RSSNR")
        val idRSRQ =cursor.getColumnIndex("RSRQ")
        if(cursor.moveToFirst()) {
            do {
                this.pRSSI = cursor.getInt(idRSSI)
                this.pRSSNR = cursor.getInt(idRSSNR)
                this.pRSRP = cursor.getInt(idRSRP)
                this.pRSRQ = cursor.getInt(idRSRQ)
            } while(cursor.moveToNext())
        }
        cursor.close()
    }

    fun json(): String {
        this.read()
        return """{"rssi":${this.pRSSI},
            "rsrp":${this.pRSRP},
            "rsrq":${this.pRSRQ},
            "network_type":"${this.networkType}",
            "sim_operator_name":"${this.simOperatorName}",
            "sim_operator":"${this.simOperator}",
            "sim_county_iso":"${this.simCountyIso}"}"""
    }

    companion object {
        private fun networkTypetoString(network: Int):String {
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

}