package ru.dimon.ydav2024

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.lang.ref.WeakReference


object Database {
    private lateinit var  context: WeakReference<Context>
    private var openHelper:DBHelper? = null
    fun setContext(context: Context){
        this.context=WeakReference(context)
        if (openHelper==null) {
            openHelper = DBHelper(context)
        }
    }
    fun getDatabase(): SQLiteDatabase{
        return openHelper!!.writableDatabase
    }


}