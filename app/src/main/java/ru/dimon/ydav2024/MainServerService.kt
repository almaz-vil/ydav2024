package ru.dimon.ydav2024

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket


class MainServerService : Service() {
    companion object {
        const val NOTIFICATION_ID=122
        private var mInstance: MainServerService? = null
        fun isServiceCreated():Boolean {
            try {
                // If instance was not cleared but the service was destroyed an Exception will be thrown
                var u=mInstance?.ping()
                if (u==null){ u=false}
                return mInstance != null && u
            } catch (e:NullPointerException) {
                // destroyed/not-started
                return false
            }
        }
    }

    fun ping():Boolean{
        return true
    }
    override fun onCreate() {
        super.onCreate()
        mInstance=this

    }
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ipHost = intent?.getStringExtra("Host")
        Log.i("ydav", "service start $ipHost")
        startForeground(NOTIFICATION_ID, newOngoingNotification(ipHost))
        val BatterInfo = Batter()
        registerReceiver(BatterInfo, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        //получение информации о телефоне и сети
        val signalStrengthm=SignalStrength(this)
        signalStrengthm.runStrength()

        Thread({
            val socketserver = ServerSocket(38300, 2, InetAddress.getByName(ipHost))
            val socket = socketserver.accept()
            val output = PrintWriter(socket.getOutputStream(), true)
            val  input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val str = input.readLine()
            Log.i("ydav", str)
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            // передать время
            val gregorianCalendar = GregorianCalendar()
            var networkType=""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val res = checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                if (res != PackageManager.PERMISSION_GRANTED) {
                    networkType = when (telephonyManager.networkType) {
                        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA -> "2G"
                                TelephonyManager.NETWORK_TYPE_1xRTT,
                        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                        else -> "Unknown"
                    }
                }
            }

            val CountConntact=0


            // из базы об состотоянии батареи
            val dbHelper = DBHelper(this)
            val db = dbHelper.readableDatabase
            val cursor: Cursor = db.query("batter", null, "name = ?", arrayOf("BATTER"), null, null, null)
            var id_temper =cursor.getColumnIndex("lavel")
            Log.d("Ydav",cursor.getString(id_temper))
            val bat_status =
                        "Y" + cursor.getString(1) + "YU" + cursor.getString(2) + "UI" + cursor.getString(3) + "IP" + cursor.getString(4) + "P"
            cursor.close()
            dbHelper.close()
            //получение информации о телефоне и сети
            val signalStrength=SignalStrength(this)
            signalStrength.readLavel()
            val res="привет;" + String.format("%tc", gregorianCalendar.getTimeInMillis()) +
                    networkType + " " + telephonyManager.simCountryIso + " " + telephonyManager.simOperatorName +
                    "R" + signalStrength.RSSI + "RA" + signalStrength.ASU + "AE" + signalStrength.ERR +
                    "E T" +signalStrength.Nomer + "T" + "S" + 0 + "S" + "B" + CountConntact.toString()+ "B" + "C" + bat_status + "CF" + 0 + "FG" + 0 + "G"
            Log.d("Ydav", res)
            output.println(res)


            socketserver.close()
        }).start()
        return START_STICKY
    }

    /*
    * Сообщение в баре системе
    * */
    private fun newOngoingNotification(ip: String?): Notification{
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val chan = NotificationChannel(
            "1986",
            "channelYdav", NotificationManager.IMPORTANCE_NONE
        )
        chan.description = "for run service Ydav"
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, "1986")
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Ydav")
            .setContentText("Работает сервер ip:$ip")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        return notification
    }

}