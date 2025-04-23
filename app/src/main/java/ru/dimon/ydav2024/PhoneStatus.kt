package ru.dimon.ydav2024

import android.database.Cursor
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Запись и чтение информации о звонке
 */
class PhoneStatus(database: Database):DbWrite {
    private val table = "caltel"
    private val _database=database

    fun delete(where :String){
        if (where.isNotEmpty()) {
            exec(_database, "DELETE FROM $table WHERE $where")
        }
    }

    fun count():Int{
        return count(_database,"SELECT count(*) as count FROM $table")
    }
    fun write(
        phone:String="",
        status:String=""
    ){
        val sql = "INSERT INTO $table(phone, status, time) VALUES ('$phone', '$status', '${timeNow()}')"
        exec(this._database, sql)

    }

    fun json():List<Phones>{
        val db = this._database.getDatabase()
        val cursor: Cursor = db.query(table, null, null, null, null, null, "_id DESC")
        val idId =cursor.getColumnIndex("_id")
        val idTime =cursor.getColumnIndex("time")
        val idPhone =cursor.getColumnIndex("phone")
        val idStatus =cursor.getColumnIndex("status")
        val phones = ArrayList<Phones>()
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(idId)
                val time = cursor.getString(idTime)
                val phone = cursor.getString(idPhone)
                val status =cursor.getString(idStatus)
                phones.add(Phones(id.toString(), time, phone, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return phones
    }
}

@Serializable
data class Phones(val id: String,
                  val time: String,
                  val phone: String,
                  val status: String)