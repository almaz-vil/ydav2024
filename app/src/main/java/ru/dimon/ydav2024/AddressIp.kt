package ru.dimon.ydav2024

import android.database.Cursor

class AddressIp(database: Database):DbWrite {

    private val _database = database

    fun setAddressIp (ip: String){
        val sql = "UPDATE address SET ip='$ip' WHERE name='server'"
        exec(this._database, sql)
    }

    fun getAddressIp(): String?{
        val db = this._database.getDatabase()
        val cursor: Cursor =
            db.query("address", null, "name = ?", arrayOf("server"), null, null, null)
        val idIp = cursor.getColumnIndex("ip")
        var ip: String? = null
        if (cursor.moveToFirst()) {
            do {
                ip = cursor.getString(idIp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ip
    }
}