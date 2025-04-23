package ru.dimon.ydav2024

import android.content.Context
import android.provider.ContactsContract
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference

class Contacts(context: Context) {

    private val _context=WeakReference(context)

    fun json():List<ContactData> {
        val context = _context.get()
        val cursor = context!!.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val idName = cursor!!.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
        val idID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
        val idPhone = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        val persion = ArrayList<ContactData>()
        val phones = ArrayList<String>()
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
                    if (cursorT!!.moveToFirst()) {
                        do {
                            val phone = cursorT.getString(cursorT.getColumnIndexOrThrow(idNumber))
                            phones.add(phone)
                        } while (cursorT.moveToNext())
                    }
                    cursorT.close()
                    persion.add(ContactData(name, phones))

                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return persion
    }


}

@Serializable
data class ContactData(val name: String, val phone: List<String>)