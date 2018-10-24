package com.swarn.components.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import android.os.AsyncTask


/**
 * @author Swarn Singh.
 */

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null

        private const val DATABASE_NAME = "Note_Database"

        fun getInstance(context: Context): NoteDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): NoteDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
            }
            return instance as NoteDatabase
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance).execute()
            }
        }
    }

    class PopulateDbAsyncTask(instance: NoteDatabase?) : AsyncTask<Void, Void, Void>() {
        private val noteDao: NoteDao? = instance?.noteDao()

        override fun doInBackground(vararg voids: Void): Void? {
            noteDao?.insert(Note("Title 1", "Description 1", 1))
            noteDao?.insert(Note("Title 2", "Description 2", 2))
            noteDao?.insert(Note("Title 3", "Description 3", 3))
            return null
        }
    }
}