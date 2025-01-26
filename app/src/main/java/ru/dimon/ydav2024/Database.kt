package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.lang.ref.WeakReference


object Database {
    private lateinit var  context: WeakReference<Context>
    private var openHeiper:DBHelper? = null
    fun setContext(context_: Context){
        context=WeakReference(context_)
        if (openHeiper==null) {
            openHeiper = DBHelper(context.get()!!)
        }
    }
    fun getDatabase(): SQLiteDatabase{
        return openHeiper!!.writableDatabase
    }
    fun closeDatabase(){
        openHeiper!!.close()
    }


}