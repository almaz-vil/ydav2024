package ru.dimon.ydav2024

import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.serialization.Serializable
import org.json.JSONObject
import java.lang.ref.WeakReference


class Contacts(context: Context) {

    private val _context=WeakReference(context)

    private fun deleteContact(contactId: String):Int{
        val context = _context.get()
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
        if (context != null) {
          return context.getContentResolver().delete(uri, null, null)
        }
        return 0
    }

    fun add(param: JSONObject):String{
        val context = _context.get()
        val name = param.getString("name")
        val phone = param.getString("phone")

        val values = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, 0)
            put(ContactsContract.RawContacts.ACCOUNT_NAME, "ydav2024")
        }
        val rawContactUri =  context!!.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
        val rawContactId = ContentUris.parseId(rawContactUri!!)
        // Вставляем имя
        ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI,this)
        }
        // Вставляем номер телефона
        ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI,this)

        }
        return rawContactId.toString()
    }

    fun delete(where: String):Int{
        var count =0
        if (where.contains(",")) {
            for (id in where.split(",")) {
               count+=deleteContact(id)
            }
        }else{
            count+=deleteContact(where)
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