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
}