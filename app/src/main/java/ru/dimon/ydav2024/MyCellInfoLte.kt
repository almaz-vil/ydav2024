package ru.dimon.ydav2024

import android.Manifest
import android.app.Service.TELEPHONY_SERVICE
import android.content.Context
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellSignalStrengthCdma
import android.telephony.CellSignalStrengthGsm
import android.telephony.CellSignalStrengthLte
import android.telephony.CellSignalStrengthNr
import android.telephony.CellSignalStrengthTdscdma
import android.telephony.CellSignalStrengthWcdma
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

@Deprecated("for Android 7")
class MyLising(context: Context,database: Database):DbWrite, PhoneStateListener() {
    private val _database=database
    private val _context=WeakReference(context).get()!!

    @Deprecated("for Android 7")
    override fun onSignalStrengthChanged(asu: Int) {
        val vRSSI = -113 + 2 * asu
        val cellParam = mutableMapOf<String, String>()
        cellParam["ASU"]=asu.toString()
        cellParam["RSSI"]=vRSSI.toString()
        val paramSignal = cellParam.toString()
        val sql = "UPDATE infolte SET param_signal='$paramSignal' WHERE name='INFOLTE'"
        exec(_database, sql)
    }
    @Deprecated("for Android 7")
    override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
        val cellParam = mutableMapOf<String, String>()
        val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val simCountyIso = telephonyManager.simCountryIso
        val simOperatorName = telephonyManager.simOperatorName
        val simOperator = telephonyManager.simOperator
        checkSelfPermission(WeakReference(_context).get()!!,Manifest.permission.READ_PHONE_STATE)
        val networkType= networkTypetoString(telephonyManager.dataNetworkType)
        cellParam["Level"]="0"
        if (signalStrength != null) {
            val vASU = signalStrength.gsmSignalStrength
            val vRSSI = -113 + 2 * vASU
            val vError = signalStrength.gsmBitErrorRate
            cellParam["Level"]=signalStrength.level.toString()
            cellParam["RSSI"]=vRSSI.toString()
            cellParam["ErrorRate"]=vError.toString()
            cellParam["ASU"]=vASU.toString()
        }
        val paramSignal = cellParam.toString()
        val sql =
            "UPDATE infolte SET sim_county_iso='$simCountyIso', sim_operator_name='$simOperatorName', sim_operator='$simOperator', network_type='$networkType', param_signal='$paramSignal' WHERE name='INFOLTE'"
        exec(_database, sql)
    }
}

    class MyCellInfoLte(context: Context, database: Database): DbWrite {
    private val _database=database
    private val _context=WeakReference(context).get()!!
    private var signalParam=""
    private var simCountyIso=""
    private var simOperatorName=""
    private var simOperator=""
    private var networkType=""

    init {
        checkSelfPermission(WeakReference(_context).get()!!,Manifest.permission.ACCESS_FINE_LOCATION)
        val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val databaseLocal = this._database

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val simCountyIso = telephonyManager.simCountryIso
            val simOperatorName = telephonyManager.simOperatorName
            val simOperator = telephonyManager.simOperator
            val networkType = networkTypetoString(telephonyManager.dataNetworkType)
            val sql =
                "UPDATE infolte SET sim_county_iso='$simCountyIso', sim_operator_name='$simOperatorName', sim_operator='$simOperator', network_type='$networkType' WHERE name='INFOLTE'"
            exec(databaseLocal, sql)
        }else{
            val lising = MyLising(_context,databaseLocal)
            telephonyManager.listen(lising, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        }
    }
    private fun updateCell(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val databaseLocal = this._database
            val telephonyManager = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val simCountyIso = telephonyManager.simCountryIso
            val simOperatorName = telephonyManager.simOperatorName
            val simOperator = telephonyManager.simOperator
            val networkType = networkTypetoString(telephonyManager.dataNetworkType)
            checkSelfPermission(WeakReference(_context).get()!!,Manifest.permission.ACCESS_FINE_LOCATION)
            val signalStrength = telephonyManager.signalStrength
            val cellSignalStrengthList=signalStrength?.cellSignalStrengths
            if (cellSignalStrengthList != null) {
                var cellParam = getCellSignalParam()
                for (cellSignalStrength in cellSignalStrengthList) {
                    try {
                        if (cellSignalStrength is CellSignalStrengthLte) cellParam = getCellSignalParam(cellSignalStrength)
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R)
                        if (cellSignalStrength is CellSignalStrengthNr) cellParam = getCellSignalParam(cellSignalStrength)
                        if (cellSignalStrength is CellSignalStrengthGsm) cellParam = getCellSignalParam(cellSignalStrength)
                        if (cellSignalStrength is CellSignalStrengthCdma) cellParam = getCellSignalParam(cellSignalStrength)
                        if (cellSignalStrength is CellSignalStrengthTdscdma) cellParam = getCellSignalParam(cellSignalStrength)
                        if (cellSignalStrength is CellSignalStrengthWcdma) cellParam = getCellSignalParam(cellSignalStrength)
                        val paramSignal = cellParam.toString()
                        val sql =
                            "UPDATE infolte SET sim_county_iso='$simCountyIso', sim_operator_name='$simOperatorName', sim_operator='$simOperator', network_type='$networkType', param_signal='$paramSignal' WHERE name='INFOLTE'"
                        exec(databaseLocal, sql)

                    } catch (e: Exception) {
                        val sql =
                            "UPDATE infolte SET sim_county_iso='$simCountyIso', sim_operator_name='$simOperatorName', sim_operator='$simOperator', network_type='$networkType', param_signal='-' WHERE name='INFOLTE'"
                        exec(databaseLocal, sql)
                    }
                }
            }
        }
    }
    private fun read(){
        updateCell()
        val db = this._database.getDatabase()
        val selection = "name = ?"
        val selectionArgs = arrayOf("INFOLTE")
        val cursor = db.query("infolte", null, selection, selectionArgs, null, null, null)
        val idParamSignal = cursor.getColumnIndex("param_signal")
        val idSimCountyIso = cursor.getColumnIndex("sim_county_iso")
        val idSimOperator = cursor.getColumnIndex("sim_operator")
        val idSimOperatorName = cursor.getColumnIndex("sim_operator_name")
        val idNetworkType = cursor.getColumnIndex("network_type")
        if(cursor.moveToFirst()) {
            do {
                this.signalParam = cursor.getString(idParamSignal)
                this.simOperator = cursor.getString(idSimOperator)
                this.networkType = cursor.getString(idNetworkType)
                this.simCountyIso = cursor.getString(idSimCountyIso)
                this.simOperatorName = cursor.getString(idSimOperatorName)
            } while(cursor.moveToNext())
        }
        cursor.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthLte): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSRP"]=cellSignalStrength.rsrp.toString()
        cellParam["RSRQ"]=cellSignalStrength.rsrq.toString()
        cellParam["RSSNR"]=cellSignalStrength.rssnr.toString()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Cqi"]=cellSignalStrength.cqi.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }

    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthGsm): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }

    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthCdma): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }

    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthNr): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }


    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthWcdma): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCellSignalParam(cellSignalStrength:  CellSignalStrengthTdscdma): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["RSCP в ASU"]=cellSignalStrength.asuLevel.toString()
        cellParam["Dbm"]=cellSignalStrength.dbm.toString()
        cellParam["Level"]=cellSignalStrength.level.toString()
        return cellParam
    }

    private fun getCellSignalParam(): Map<String, String>{
        val cellParam = mutableMapOf<String, String>()
        cellParam["Level"]="0"
        return cellParam
    }
        fun json(): MyCellInfoLteData {
            this.read()
            val sParam = if (this.signalParam.length>2){this.signalParam.drop(1).dropLast(1)} else {this.signalParam}
            val sP = sParam.replace(",","")
            return MyCellInfoLteData(sP, networkType, simOperatorName, simOperator, simCountyIso)
        }

    }

@Serializable
data class MyCellInfoLteData(val signal_param: String,
                             val network_type: String,
                             val sim_operator_name: String,
                             val sim_operator: String,
                             val sim_county_iso: String)
