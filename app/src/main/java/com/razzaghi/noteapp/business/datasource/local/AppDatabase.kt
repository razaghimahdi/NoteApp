package com.razzaghi.noteapp.business.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
