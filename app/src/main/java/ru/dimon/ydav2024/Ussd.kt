package ru.dimon.ydav2024

import android.Manifest
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

@Serializable
data class UssdData(val failure: String, val request: String){}

@RequiresApi(Build.VERSION_CODES.O)
class UssdCallBack(database: Database):DbWrite, TelephonyManager.UssdResponseCallback() {
    private val _database=database
    override fun onReceiveUssdResponseFailed(
        telephonyManager: TelephonyManager?,
        request: String?,
        failureCode: Int
    ) {
        val sql =
            "UPDATE ussd SET failure_code='${failureCode.toString()}' WHERE name='USSD'"
        exec(_database, sql)
     }

    override fun onReceiveUssdResponse(
        telephonyManager: TelephonyManager?,
        request: String?,
        response: CharSequence?
    ) {
        val sql =
            "UPDATE ussd SET response='${response.toString()}' WHERE name='USSD'"
        exec(_database, sql)
    }
}

class Ussd(context: Context, database: Database): DbWrite {
    private val _database=database
    private val _context= WeakReference(context).get()!!
    private var failureCode=""
    private var response=""

    fun send(ussd: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         zero()
         val telephonyManager  = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
         val databaseLocal = this._database
         val callBack = UssdCallBack(databaseLocal)
         val handler = Handler(Looper.getMainLooper())
         checkSelfPermission(WeakReference(_context).get()!!, Manifest.permission.CALL_PHONE)
         telephonyManager.sendUssdRequest(ussd, callBack, handler)
        }
    }
    private fun zero(){
        val sql =
            "UPDATE ussd SET failure_code='0', response='0' WHERE name='USSD'"
        exec(_database, sql)

    }
    private fun read(){
        val db = this._database.getDatabase()
        val selection = "name = ?"
        val selectionArgs = arrayOf("USSD")
        val cursor = db.query("ussd", null, selection, selectionArgs, null, null, null)
        val idFailureCode = cursor.getColumnIndex("failure_code")
        val idResponse = cursor.getColumnIndex("response")
        if(cursor.moveToFirst()) {
            do {
                this.failureCode = cursor.getString(idFailureCode)
                this.response = cursor.getString(idResponse)
            } while(cursor.moveToNext())
        }
        cursor.close()
    }

    fun json(): UssdData {
        this.read()
        return UssdData(this.failureCode, this.response)
    }
}