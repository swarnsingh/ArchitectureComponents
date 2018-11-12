package com.swarn.components.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.swarn.components.data.Note
import com.swarn.components.data.NoteRepository
import io.reactivex.Completable
import io.reactivex.Flowable


/**
 * @author Swarn Singh.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository?
    private val allNotes: Flowable<List<Note>>?

    init {
        repository = NoteRepository(application)
        allNotes = repository.getAllNotes()
    }

    fun insert(note: Note): Completable? {
        return repository?.insert(note)
    }

    fun update(note: Note): Completable? {
        return repository?.update(note)
    }

    fun delete(note: Note): Completable? {
        return repository?.delete(note)
    }

    fun deleteAllNotes(): Completable? {
        return repository?.deleteAllNotes()
    }

    fun getAllNotes(): Flowable<List<Note>>? {
        return allNotes
    }
}