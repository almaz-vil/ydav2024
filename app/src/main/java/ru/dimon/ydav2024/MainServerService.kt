package ru.dimon.ydav2024

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.serialization.json.Json
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
import java.util.Formatter
import java.util.GregorianCalendar


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
        //отправка СМС и их статус
        val smsOutput = SmsOutput(refConnect.get()!!,Database)
        //USSD - команды
        val ussdCommand = Ussd(refConnect.get()!!,Database)
        //Контакты
        val contacts = Contacts(refConnect.get()!!)
        Thread{
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
                                var command: String
                                var json: JSONObject? = null
                                try {
                                    json = JSONTokener(inputJson).nextValue() as JSONObject
                                    command = json.getString("command")
                                }
                                catch (e: Exception){
                                    command = "INFO"
                                }
                                val gregorianCalendar = GregorianCalendar()
                                val formatter = Formatter()
                                val t=gregorianCalendar.timeInMillis
                                val timeSend =  formatter.format("%tF %tT ",t, t)
                                val outJson = when (command) {
                                    "INFO" -> {
                                        Json.encodeToString(Info(
                                            time = timeSend.toString(),
                                            battery = battery.json(),
                                            signal = myCellInfoLte.json(),
                                            sms = smsInput.count(),
                                            phone = phoneStatus.count()))
                                    }

                                    "PHONE" -> {
                                        //информация о звонках
                                        Json.encodeToString(Phone(
                                            time = timeSend.toString(),
                                            phone = phoneStatus.json()))
                                    }

                                    "DELETE_PHONE" -> {
                                        //Удаление входящих звонков
                                        val param = json?.getString("param")
                                        if (param != null)
                                            phoneStatus.delete(param)
                                        Json.encodeToString(DeletePhone(
                                            time = timeSend.toString(),
                                            phone = phoneStatus.count()))
                                    }

                                    "DELETE_CONTACT" -> {
                                        //Удаление контактов
                                        val param = json?.getString("param")
                                        var count = 0
                                        if (param != null)
                                            count=contacts.delete(param)
                                        //выборка контактов
                                        Json.encodeToString(ContactCount(
                                            time = timeSend.toString(),
                                            count = count
                                        ))
                                    }

                                    "ADD_CONTACT" -> {
                                        //Добавление контакта
                                        val param = json?.getString("param")
                                        var id = ""
                                        if (param != null)
                                            id=contacts.add(JSONObject(param))
                                        //ID контакта
                                        Json.encodeToString(ContactID(
                                            time = timeSend.toString(),
                                            ID = id
                                        ))
                                    }

                                    "DELETE_SMS_INPUT" -> {
                                        //Удаление входящих СМС
                                        val param = json?.getString("param")
                                        Log.d("Ydav2024", "onStartCommand: $param")
                                        if (param != null)
                                                smsInput.delete(param)
                                        Json.encodeToString(DeleteSmsInput(
                                            time = timeSend.toString(),
                                            sms = smsInput.count()
                                        ))
                                     }

                                    "SMS_OUTPUT" -> {
                                        //Отправка СМС
                                        val param = json?.getString("param")
                                        var id = "1"
                                        if (param != null) {
                                            id = smsOutput.send(JSONObject(param))
                                        }
                                        Json.encodeToString(SmsOutputD(
                                            time = timeSend.toString(),
                                            status = smsOutput.json(id)
                                        ))
                                    }

                                    "SMS_OUTPUT_STATUS" -> {
                                        //Получение статуса отправленной СМС
                                        val id = json?.getString("param")
                                        Json.encodeToString(SmsOutputD(
                                            time = timeSend.toString(),
                                            status = smsOutput.json(id!!)
                                        ))
                                    }

                                    "SMS_INPUT" -> {
                                        //выборка входящий СМС
                                        Json.encodeToString(SmsInputD(
                                            time = timeSend.toString(),
                                            sms = smsInput.json()
                                        ))
                                    }

                                    "USSD_SEND" -> {
                                        //Отправка USSD
                                        val ussdText = json?.getString("param")
                                        ussdCommand.send(ussdText)
                                        Json.encodeToString(UssdSend(
                                            time = timeSend.toString(),
                                            ussd = ussdCommand.json()
                                        ))
                                    }

                                    "USSD_RESPOND" -> {
                                        //USSD ответ
                                        Json.encodeToString(UssdSend(
                                            time = timeSend.toString(),
                                            ussd = ussdCommand.json()
                                        ))
                                    }

                                    "CONTACT" -> {
                                        //выборка контактов
                                        Json.encodeToString(ContactD(
                                            time = timeSend.toString(),
                                            contact = contacts.json()
                                        ))
                                    }
                                    else -> {
                                        Json.encodeToString(HostD(
                                            time = timeSend.toString(),
                                            ipHost = ipHost!!
                                        ))
                                    }
                                }
                                output.println(outJson)
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