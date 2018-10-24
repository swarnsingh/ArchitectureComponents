package com.swarn.components.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.swarn.components.data.Note
import com.swarn.components.data.NoteRepository


/**
 * @author Swarn Singh.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository?
    private val allNotes: LiveData<List<Note>>?

    init {
        repository = NoteRepository(application)
        allNotes = repository.getAllNotes()
    }

    fun insert(note: Note) {
        repository?.insert(note)
    }

    fun update(note: Note) {
        repository?.update(note)
    }

    fun delete(note: Note) {
        repository?.delete(note)
    }

    fun deleteAllNotes() {
        repository?.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<Note>>? {
        return allNotes
    }
}