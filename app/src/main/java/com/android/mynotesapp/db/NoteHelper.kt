package com.android.mynotesapp.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.provider.BaseColumns
import com.android.mynotesapp.db.DatabaseContract.Notecolums.Companion.DATE
import com.android.mynotesapp.db.DatabaseContract.Notecolums.Companion.DESCRIPTION
import com.android.mynotesapp.db.DatabaseContract.Notecolums.Companion.TABLE_NAME
import com.android.mynotesapp.db.DatabaseContract.Notecolums.Companion.TITLE
import com.android.mynotesapp.entity.Note
import java.util.*

class NoteHelper(context: Context) {
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: NoteHelper? = null

        fun getInstance(context: Context): NoteHelper = INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoteHelper(context)
            }
    }

    @Throws(SQLiteException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }
    fun close() {
        databaseHelper.close()
        if (database.isOpen) {
            database.close()
        }
    }

    /**
     * Ambil data dari semua note yang ada di dalam database
     *
     * @return cursor hasil queryAll
     */
    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${BaseColumns._ID} ASC"
        )
    }

    /**
     * Ambil data dari note berdasarakan parameter id
     *
     * @param id id note yang dicari
     * @return cursor hasil queryAll
     */
    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(id),
            null,
            null,
            null,
            null)
    }

    /**
     * Simpan data ke dalam database
     *
     * @param values nilai data yang akan di simpan
     * @return long id dari data yang baru saja di masukkan
     */
    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    /**
     * Update data dalam database
     *
     * @param id     data dengan id berapa yang akan di update
     * @param values nilai data baru
     * @return int jumlah data yang ter-update
     */
    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "${BaseColumns._ID} = ?", arrayOf(id))
    }

    /**
     * Delete data dalam database
     *
     * @param id data dengan id berapa yang akan di delete
     * @return int jumlah data yang ter-delete
     */
    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "${BaseColumns._ID} = '$id'", null)
    }

    /**
     * Gunakan method ini untuk ambil semua note yang ada
     * Otomatis di parsing ke dalam model Note
     *
     * @return hasil getGetAllNotes berbentuk array model note
     */
    fun getAllNotes(): ArrayList<Note> {
        val arrayList = ArrayList<Note>()
        val cursor = database.query(DATABASE_TABLE, null, null, null, null, null,
            "${BaseColumns._ID} ASC", null)
        cursor.moveToFirst()
        var note: Note
        if (cursor.count > 0) {
            do {
                note = Note()
                note.id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                note.title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE))
                note.description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION))
                note.date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))

                arrayList.add(note)
                cursor.moveToNext()

            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    /**
     * Gunakan method ini untuk insertNote
     *
     * @param note model note yang akan dimasukkan
     * @return id dari data yang baru saja dimasukkan
     */
    fun insertNote(note: Note): Long {
        val args = ContentValues()
        args.put(TITLE, note.title)
        args.put(DESCRIPTION, note.description)
        args.put(DATE, note.date)
        return database.insert(DATABASE_TABLE, null, args)
    }


    /**
     * Gunakan method ini untuk updateNote
     *
     * @param note model note yang akan diubah
     * @return int jumlah dari row yang ter-updateNote, jika tidak ada yang diupdate maka nilainya 0
     */
    fun updateNote(note: Note): Int {
        val args = ContentValues()
        args.put(TITLE, note.title)
        args.put(DESCRIPTION, note.description)
        args.put(DATE, note.date)
        return database.update(DATABASE_TABLE, args, BaseColumns._ID + "= '" + note.id + "'", null)
    }

    /**
     * Gunakan method ini untuk deleteNote
     *
     * @param id id yang akan di deleteNote
     * @return int jumlah row yang di deleteNote
     */
    fun deleteNote(id: Int): Int {
        return database.delete(TABLE_NAME, "${BaseColumns._ID} = '$id'", null)
    }

}