package ru.dimon.ydav2024

import android.Manifest
import android.app.Service.TELEPHONY_SERVICE
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.checkSelfPermission
import java.lang.ref.WeakReference
import java.util.concurrent.Executor

class MyCellInfoLte(context: Context, database: Database): DbWrite {
    private val _database=database
    private val _context=WeakReference(context).get()!!
    private val _mainExecute=context.mainExecutor
    private var pRSSI=-1
    private var pRSRP=-1
    private var pRSSNR=-1
    private var pRSRQ=-1
    private var simCountyIso=""
    private var simOperatorName=""
    private var simOperator=""
    private var networkType=""

    init {
        checkSelfPermission(WeakReference(_context).get()!!,Manifest.permission.ACCESS_FINE_LOCATION)
        val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        this.simCountyIso = telephonyManager.simCountryIso
        this.simOperatorName = telephonyManager.simOperatorName
        this.simOperator = telephonyManager.simOperator
        this.networkType = networkTypetoString(telephonyManager.dataNetworkType)
        val database_local = this._database
        telephonyManager.requestCellInfoUpdate(WeakReference(_mainExecute).get()!!, object : TelephonyManager.CellInfoCallback() {
            override fun onCellInfo(activeCellInfo: MutableList<CellInfo>) {
                for (cellInfo in activeCellInfo) {
                    val cellInfoLte  = cellInfo as CellInfoLte
                    val vRSRP=cellInfoLte.cellSignalStrength.rsrp
                    val vRSSI=cellInfoLte.cellSignalStrength.rssi
                    val vRSSNR=cellInfoLte.cellSignalStrength.rssnr
                    val vRSRQ=cellInfoLte.cellSignalStrength.rsrq
                    val sql = "UPDATE infolte SET RSRP=$vRSRP, RSSI=$vRSSI, RSSNR=$vRSSNR, RSRQ=$vRSRQ WHERE name='INFOLTE'"
                    exec(database_local, sql)
                }
            }
        })
    }

    private fun read(){
        val db = this._database.getDatabase()
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