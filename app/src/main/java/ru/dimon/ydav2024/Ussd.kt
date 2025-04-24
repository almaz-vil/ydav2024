package ru.dimon.ydav2024

import android.content.Context
import android.content.Intent
import android.util.Log
import java.lang.ref.WeakReference

class Ussd(context: Context, database: Database): DbWrite {
    private val _database=database
    private val _context=WeakReference(context).get()
    private var failureCode=""
    private var response=""

    fun send(ussd: String?) {
            zero()
            val ussdSendService =
                Intent(WeakReference(_context).get(), UssdSendService::class.java)
            ussdSendService.putExtra("ussd", ussd)
            _context!!.startService(ussdSendService)
    }
    private fun zero(){
        val sql =
            "UPDATE ussd SET failure_code='', response='' WHERE name='USSD'"
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