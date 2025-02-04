package ru.dimon.ydav2024

import android.content.Context
import android.provider.ContactsContract
import java.lang.ref.WeakReference

class Contacts(context: Context) {

    private val _context=WeakReference(context)

    fun json():String {
        var jsonText =""
        val context = _context.get()
        val cursor = context!!.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val idName = cursor!!.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
        val idID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
        val idPhone = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(idName)
                val id = cursor.getString(idID)
                val countPhone = cursor.getInt(idPhone)
                if (countPhone > 0) {
                    val cursorT = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    val idNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
                    var jsonPhone = ""
                    if (cursorT!!.moveToFirst()) {
                        do {
                            val phone = cursorT.getString(cursorT.getColumnIndexOrThrow(idNumber))
                            jsonPhone+=""""$phone","""
                        } while (cursorT.moveToNext())
                    }
                    cursorT.close()
                    jsonText+="""{"name":"$name", "phone":[${jsonPhone.dropLast(1)}]},"""

                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return  "["+jsonText.dropLast(1)+"]"
    }

}