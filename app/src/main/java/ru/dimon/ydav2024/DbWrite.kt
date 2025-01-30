package ru.dimon.ydav2024

interface DbWrite {
    fun exec(database: Database, sql: String){
        val db = database.getDatabase()
        db.beginTransactionNonExclusive()
        try {
            db.execSQL(sql)
            db.setTransactionSuccessful()
        }finally {
            db.endTransaction()
        }
    }

    fun count(database: Database, sql: String):Int{
        val db = database.getDatabase()
        val cursor = db.rawQuery(sql,null)
        var res = 0
        val idCount = cursor.getColumnIndex("count")
        if (cursor.moveToNext()){
            res = cursor.getInt(idCount)
        }
        cursor.close()
        return res
    }
}