package ru.dimon.ydav2024

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.telephony.SmsManager
import kotlinx.serialization.Serializable
import org.json.JSONObject

class SmsOutput(context: Context, database: Database):DbWrite {

    private val table = "outputSms"
    private val _context = context
    private val _database = database

    private var id = ""
    private var phone = ""
    private var text = ""
    private var sent = "none"
    private var delivery = "none"
    private var sentTime = ""
    private var deliveryTime = ""


    private fun read(){
        val db = this._database.getDatabase()
        val cursor: Cursor =
            db.query(this.table, null, "id = ?", arrayOf(this.id), null, null, null)
        val idSent = cursor.getColumnIndex("sent")
        val idDelivery = cursor.getColumnIndex("delivery")
        val idSentTime = cursor.getColumnIndex("sent_time")
        val idDeliveryTime = cursor.getColumnIndex("delivery_time")
        if (cursor.moveToFirst()) {
            do {
                this.sent = cursor.getString(idSent)
                this.delivery = cursor.getString(idDelivery)
                this.sentTime = cursor.getString(idSentTime)
                this.deliveryTime = cursor.getString(idDeliveryTime)
            } while (cursor.moveToNext())
        }
        cursor.close()

    }

    fun writeSent(id: String, sent: String){
        val sql = "UPDATE $table SET sent='$sent', sent_time='${timeNow("HH:mm:ss dd-MM")}' WHERE id='$id'"
        exec(_database, sql)
    }

    fun writeDelivery(id: String, delivery: String){
        val sql = "UPDATE $table SET delivery='$delivery', delivery_time='${timeNow("HH:mm:ss dd-MM")}' WHERE id='$id'"
        exec(_database, sql)
    }

    fun send(param: JSONObject):String{
        this.id = param.getString("id")
        this.phone = param.getString("phone")
        this.text =  param.getString("text")
        val sql = "INSERT INTO $table(phone, text, id, sent, delivery, sent_time, delivery_time) VALUES ('$phone', '$text', '$id', 'none', 'none', '', '')"
        exec(_database, sql)
        sendSms()
        return this.id
    }

    private fun sendSms(){
        //Для получения результата отправки СМС
        val sentIntent = Intent(_context.getString(R.string.sent_sms_action))
        sentIntent.putExtra("id", this.id)
        val sendIntentPending = PendingIntent.getBroadcast(
            this._context,
            this.id.toInt(),
            sentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        //Для получения результата получения СМС
        val deliveryIntent = Intent(_context.getString(R.string.delivery_sms_action))
        deliveryIntent.putExtra("id", this.id)
        val deliveryIntentPending = PendingIntent.getBroadcast(
            this._context,
            this.id.toInt(),
            deliveryIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            _context.getSystemService<SmsManager>(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }

        smsManager.sendTextMessage(
            this.phone,
            null,
            this.text,
            sendIntentPending,
            deliveryIntentPending)
    }

    fun json(id: String):SmsOutputData{
        this.id = id
        this.read()
        return SmsOutputData(
            id = this.id.toInt(),
            sent = ResultStatus(
                result = this.sent,
                time = this.sentTime
            ),
            delivery = ResultStatus(
                result = this.delivery,
                time = this.deliveryTime
            )
            )
    }

}

@Serializable
data class SmsOutputData( val id: Int,
                            val sent: ResultStatus,
                            val delivery: ResultStatus)
@Serializable
data class ResultStatus(val result: String,
                        val time: String)