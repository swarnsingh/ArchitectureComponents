package com.swarn.components.data

import android.app.Application
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * @author Swarn Singh.
 */
class NoteRepository {

    private var noteDao: NoteDao? = null
    private var allNotes: Flowable<List<Note>>? = null

    constructor(application: Application) {
        val database = NoteDatabase.getInstance(application)
        noteDao = database.noteDao()
        allNotes = noteDao?.getAllNotes()
    }

    fun insert(note: Note): Completable? {
        return Completable.fromAction {
            noteDao?.insert(note)
        }
    }

    fun update(note: Note): Completable? {
        return Completable.fromAction {
            noteDao?.update(note)
        }
    }

    fun delete(note: Note): Completable? {
        return Completable.fromAction {
            noteDao?.delete(note)
        }
    }

    fun deleteAllNotes(): Completable? {
        return Completable.fromAction {
            noteDao?.deleteAllNotes()
        }
    }

    fun getAllNotes(): Flowable<List<Note>>? {
        return allNotes
    }
}
