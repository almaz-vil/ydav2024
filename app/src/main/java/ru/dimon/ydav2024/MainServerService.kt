package ru.dimon.ydav2024

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
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
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainServerService : Service() {
    companion object {
        const val NOTIFICATION_ID=122
        private var mInstance: MainServerService? = null

        fun isServiceCreated():Boolean {
            try {
                var u=mInstance?.ping()
                if (u==null){
                    u=false
                }
                return mInstance != null && u
            } catch (e:NullPointerException) {
                return false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mInstance=null
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
        startForeground(NOTIFICATION_ID, newOngoingNotification(ipHost))
        Thread{
            val refConnect = WeakReference(this.applicationContext)
            Database.setContext(refConnect.get()!!)
            val batteryBroadcastReceiver = BatteryBroadcastReceiver()
            // об состоянии батареи
            val battery = Battery(Database)
            //получение информации о сети
            val myCellInfoLte = MyCellInfoLte(refConnect.get()!!,Database)
            //информация о входящих звонках
            val phoneStatus = PhoneStatus(Database)
            //выборка входящий СМС
            val smsInput = SmsInput(Database)

            var connectWifi = true
            while (connectWifi){
                var server: ServerSocket?
                try {
                    server = ServerSocket(38300, 2, InetAddress.getByName(ipHost))
                    //Для учёта изменений состояния батареи
                    registerReceiver(batteryBroadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    //Отправить IP адрес в уведомление
                    startForeground(NOTIFICATION_ID, newOngoingNotification("IP адрес сервера: $ipHost"))
                } catch (e: Exception){
                    server = null
                    connectWifi = false
                }
                server?.use {
                    do {
                        var socket: Socket?
                        try {
                            //Ожидание клиента к серверу
                            socket = server.accept()
                        } catch (e: Exception) {
                            //Отправить IP адрес в Activity
                            startForeground(NOTIFICATION_ID, newOngoingNotification("Сервер остановлен! Нет WiFi"))
                            unregisterReceiver(batteryBroadcastReceiver)
                            socket = null
                            connectWifi = false
                        }
                        if (socket != null) {
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
                                val time = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy EEEE HH:mm:ss ")
                                val timeSend =  time.format(formatter)
                                when (command) {
                                    "INFO" -> {
                                        val inf = """{"time":"$timeSend",
                                       "battery":${battery.json()},
                                       "signal":${myCellInfoLte.json()},
                                       "sms":${smsInput.count()},
                                       "phone":${phoneStatus.count()}}
                                                                   
                                      """
                                        output.println(inf)

                                    }

                                    "PHONE" -> {
                                        //информация о звонках
                                        val inf = """{"time":"$timeSend",
                                       "phone":${phoneStatus.json()}}
                                                                   
                                       """
                                        output.println(inf)
                                    }

                                    "DELETE_PHONE" -> {
                                        //Удаление входящих звонков
                                        phoneStatus.delete(param)
                                        val inf = """{"time":"$timeSend",
                                       "phone":${phoneStatus.count()}}
                                                                   
                                      """
                                        output.println(inf)
                                    }

                                    "DELETE_SMS_INPUT" -> {
                                        //Удаление входящих СМС
                                        smsInput.delete(param)
                                        val inf = """{"time":"$timeSend",
                                       "sms":${smsInput.count()}}
                                                                   
                                      """
                                        output.println(inf)
                                    }

                                    "SMS_INPUT" -> {
                                        //выборка входящий СМС
                                        val inf = """{"time":"$timeSend",
                                       "sms":${smsInput.json()}}
                                                                   
                                       """
                                        output.println(inf)
                                    }

                                    "CONTACT" -> {
                                        //выборка контактов
                                        val contacts = Contacts(refConnect.get()!!)
                                        val inf = """{"time":"$timeSend",
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

                            } catch (e: IOException) {
                                socket.close()
                            }
                        }
                    } while (connectWifi)
                }
            }
        }.start()
        return START_STICKY
    }

    /*
    * Сообщение в баре системе
    * */
    private fun newOngoingNotification(message: String?): Notification{
        val channelId = getString(R.string.channel_id)
        val context = WeakReference( this@MainServerService.applicationContext).get()!!
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        return notification
    }



}