package com.android.mynotesapp.adapter

import android.database.Cursor
import com.android.mynotesapp.db.DatabaseContract
import com.android.mynotesapp.entity.Note

object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.Notecolums._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.DATE))
                notesList.add(Note(id, title, description, date))
            }
        }
        return notesList
    }
    fun mapCursorToObject(notesCursor: Cursor?): Note {
        var note = Note()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.Notecolums._ID))
            val title = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.TITLE))
            val description = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(DatabaseContract.Notecolums.DATE))
            note = Note(id, title, description, date)
        }
        return note
    }
}