package ru.dimon.ydav2024

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.GregorianCalendar
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.ref.WeakReference
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
        val batteryBroadcastReceiver = BatteryBroadcastReceiver()
        registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val statusCallBroadcastReceiver = StatusCallBroadcastReceiver()
        registerReceiver(statusCallBroadcastReceiver, IntentFilter("android.intent.action.PHONE_STATE"))
        val smsInputBroadcastReceiver = SmsInputBroadcastReceiver()
        registerReceiver(smsInputBroadcastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        Thread{
            val server = ServerSocket(38300, 2, InetAddress.getByName(ipHost))
            val ref_connect = WeakReference(this.applicationContext)
            Database.setContext(ref_connect.get()!!)
            // об состоянии батареи
            val battery = Battery(Database)
            //получение информации о сети
            val myCellInfoLte = MyCellInfoLte(ref_connect.get()!!,Database)
            //информация о входящих звонках
            val phoneStatus = PhoneStatus(Database)
            //выборка входящий СМС
            val smsInput = SmsInput(Database)
            try{
                while (true) {
                    val socket = server.accept()
                    try {
                        val outputStream = socket.getOutputStream()
                        val output = PrintWriter(outputStream, true)
                        val inputStream = socket.getInputStream()
                        val inputStreamReader = InputStreamReader(inputStream)
                        val input = BufferedReader(inputStreamReader)
                        val inputJson = input.readLine()
                        val json = JSONTokener(inputJson).nextValue() as JSONObject
                        val command = json.getString("command")
                        val param = json.getString("param")
                        when (command) {
                            "INFO" -> {
                                val inf = """{"time":"${
                                    String.format(
                                        "%tc",
                                        GregorianCalendar().timeInMillis
                                    )
                                }",
                                       "battery":${battery.json()},
                                       "signal":${myCellInfoLte.json()},
                                       "sms":${smsInput.count()}}
                                                                   
                                      """
                                output.println(inf)

                            }

                            "PHONE" -> {
                                //информация о звонках
                                val inf = """{"time":"${
                                    String.format(
                                        "%tc",
                                        GregorianCalendar().timeInMillis
                                    )
                                }",
                                       "phone":${phoneStatus.json()}}
                                                                   
                                       """
                                output.println(inf)
                            }

                            "DELETE_SMS_INPUT" -> {
                                //Удаление входящих СМС
                                smsInput.delete(param)
                                val inf = """{"time":"${
                                    String.format(
                                        "%tc",
                                        GregorianCalendar().timeInMillis
                                    )
                                }",
                                       "sms":${smsInput.count()}}
                                                                   
                                      """
                                output.println(inf)
                            }

                            "SMS_INPUT" ->{
                                //выборка входящий СМС
                                val inf = """{"time":"${
                                    String.format(
                                        "%tc",
                                        GregorianCalendar().timeInMillis
                                    )
                                }",
                                       "sms":${smsInput.json()}}
                                                                   
                                       """
                                output.println(inf)
                            }

                            "CONTACT" -> {
                                //выборка контактов
                                val contacts = Contacts(ref_connect.get()!!)
                                val inf = """{"time":"${
                                    String.format(
                                        "%tc",
                                        GregorianCalendar().timeInMillis
                                    )
                                }",
                                       "contact":${contacts.json()}}
                                                                   
                                       """
                                output.println(inf)
                            }
                        }
                        output.close()
                        outputStream.close()
                        input.close()
                        inputStream.close()
                        inputStreamReader.close()

                    } catch (e :IOException){
                        socket.close()
                    }
               }
            } finally {
                server.close()
            }

        }.start()
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