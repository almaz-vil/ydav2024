package ru.dimon.ydav2024

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.serialization.Serializable
import java.lang.ref.WeakReference


class Contacts(context: Context) {

    private val _context=WeakReference(context)

    private fun deleteContact(contactId: String):Int{
        val context = _context.get()
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
        if (context != null) {
          return  context.getContentResolver().delete(uri, null, null)
        }
        return 0
    }

    fun delete(where: String):Int{
        var count =0
        if (where.contains(",")) {
            for (id in where.split(",")) {
               count=count+deleteContact(id)
            }
        }else{
            count=count+deleteContact(where)
        }
        return count
    }
    fun json():List<ContactData> {
        val context = _context.get()
        val cursorContact = context!!.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val idName = cursorContact!!.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val idID = cursorContact.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
        val idPhone = cursorContact.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        val idNumber = ContactsContract.CommonDataKinds.Phone.NUMBER
        val persion = ArrayList<ContactData>()
        if (cursorContact.moveToFirst()) {
            do {
                val phones = ArrayList<String>()
                val name = cursorContact.getString(idName)
                val id = cursorContact.getString(idID)
                val countPhone = cursorContact.getInt(idPhone)
                if (countPhone > 0) {
                    val cursorPhone = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                         arrayOf(id),
                        null
                    )
                    if (cursorPhone!!.moveToFirst()) {
                        do {
                            val phone = cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(idNumber)).replace(" ","")
                            phones.add(phone.replace("-",""))
                        } while (cursorPhone.moveToNext())
                    }
                    cursorPhone.close()
                    persion.add(ContactData(id!!, name, phones.distinct()))
                }
            } while (cursorContact.moveToNext())
        }
        cursorContact.close()
        return persion.distinct()
    }

}

@Serializable
data class ContactData(val id: String, val name: String, val phone: List<String>)