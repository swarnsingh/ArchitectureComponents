package com.swarn.components.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

/**
 * @author Swarn Singh.
 */
class NoteRepository {

    private var noteDao: NoteDao? = null
    private var allNotes: LiveData<List<Note>>? = null

    constructor(application: Application) {
        val database = NoteDatabase.getInstance(application)
        noteDao = database.noteDao()
        allNotes = noteDao?.getAllNotes()
    }

    fun insert(note: Note) {
        InsertNoteAsyncTask(noteDao).execute(note)
    }

    fun update(note: Note) {
        UpdateNoteAsyncTask(noteDao).execute(note)
    }

    fun delete(note: Note) {
        DeleteNoteAsyncTask(noteDao).execute(note)
    }

    fun deleteAllNotes() {
        DeleteAllNotesAsyncTask(noteDao).execute()
    }

    fun getAllNotes(): LiveData<List<Note>>? {
        return allNotes
    }

    private class InsertNoteAsyncTask constructor(private val noteDao: NoteDao?) : AsyncTask<Note, Void, Void>() {

        override fun doInBackground(vararg notes: Note): Void? {
            noteDao?.insert(notes[0])
            return null
        }
    }

    private class UpdateNoteAsyncTask constructor(private val noteDao: NoteDao?) : AsyncTask<Note, Void, Void>() {

        override fun doInBackground(vararg notes: Note): Void? {
            noteDao?.update(notes[0])
            return null
        }
    }

    private class DeleteNoteAsyncTask constructor(private val noteDao: NoteDao?) : AsyncTask<Note, Void, Void>() {

        override fun doInBackground(vararg notes: Note): Void? {
            noteDao?.delete(notes[0])
            return null
        }
    }

    private class DeleteAllNotesAsyncTask constructor(private val noteDao: NoteDao?) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            noteDao?.deleteAllNotes()
            return null
        }
    }
}
