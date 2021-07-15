package com.android.mynotesapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.android.mynotesapp.db.DatabaseContract.Notecolums.Companion.TABLE_NAME

internal class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbnoteapp"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                "(${DatabaseContract.Notecolums._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DatabaseContract.Notecolums.TITLE} TEXT NOT NULL," +
                "${DatabaseContract.Notecolums.DESCRIPTION} TEXT NOT NULL," +
                "${DatabaseContract.Notecolums.DATE} TEXT NOT NULL)"

    }
    /*
    Method onUpgrade akan di panggil ketika terjadi perbedaan versi
    Gunakan method onUpgrade untuk melakukan proses migrasi data
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_NOTE)
    }
    /*
    Drop table tidak dianjurkan ketika proses migrasi terjadi dikarenakan data user akan hilang,
    */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}