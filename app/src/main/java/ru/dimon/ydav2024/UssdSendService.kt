package ru.dimon.ydav2024

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

class UssdSendService: Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val ussdCommand = intent.extras!!.getString("ussd") as String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val _context = WeakReference(this@UssdSendService).get()!!
            Database.setContext(_context)
            val telephonyManager  = _context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val callBack = UssdCallBack(Database)
            val handler = Handler()
            ContextCompat.checkSelfPermission(
                WeakReference(_context).get()!!,
                Manifest.permission.CALL_PHONE
            )
            telephonyManager.sendUssdRequest(ussdCommand, callBack, handler)
        }
        return START_NOT_STICKY
    }
}



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
