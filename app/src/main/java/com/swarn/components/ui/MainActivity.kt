package com.swarn.components.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.swarn.components.R
import com.swarn.components.adapters.NoteAdapter
import com.swarn.components.data.Note
import com.swarn.components.viewmodels.NoteViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Swarn Singh.
 */
class MainActivity : AppCompatActivity() {

    private var noteViewModel: NoteViewModel? = null

    private val disposable = CompositeDisposable()

    companion object {
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)

        buttonAddNote.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = NoteAdapter()
        recyclerView.adapter = adapter

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        /* noteViewModel?.getAllNotes()?.observe(this, Observer<List<Note>> {
             adapter.setNotes(it)
         })*/

        disposable.add(noteViewModel?.getAllNotes()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ adapter.setNotes(it) },
                        { error -> Log.e(TAG, "Unable to get Notes", error) })!!)


        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note: Note = adapter.notes[viewHolder.adapterPosition]

                disposable.add(noteViewModel?.delete(note)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe()!!)

                Toast.makeText(applicationContext, "${note.title} deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.description)
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.priority)
                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val title = data!!.getStringExtra(AddNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1)

            val note = Note(title, description, priority)

            disposable.add(noteViewModel?.insert(note)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe { Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show() }!!)

        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            val id = data!!.getIntExtra(AddNoteActivity.EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            val title = data!!.getStringExtra(AddNoteActivity.EXTRA_TITLE)
            val description = data!!.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION)
            val priority = data!!.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1)

            val note = Note(title, description, priority)
            note.id = id

            disposable.add(noteViewModel?.update(note)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe { Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show() }!!)


        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_notes -> {
                disposable.add(noteViewModel?.deleteAllNotes()
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe { Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show() }!!)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
