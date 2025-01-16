package ru.dimon.ydav2024

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

class Contacts(context: Context) {

    private val _context=context

    fun json():String {
        var jsonText =""
        val cursor = _context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val idName = cursor!!.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
        val idID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
        val idPhone = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(idName)
                val id = cursor.getString(idID)
                val count_phone = cursor.getInt(idPhone)
                if (count_phone > 0) {
                    val cursor_t = _context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    val id_numer = ContactsContract.CommonDataKinds.Phone.NUMBER
                    if (cursor_t!!.moveToFirst()) {
                        do {
                            val phone = cursor_t.getString(cursor_t.getColumnIndexOrThrow(id_numer))
                             jsonText+="""{"name":"$name",
                                      "phone":"$phone"},"""
                        } while (cursor_t.moveToNext())
                    }
                    cursor_t.close()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return  "["+jsonText.dropLast(1)+"]"
    }

}