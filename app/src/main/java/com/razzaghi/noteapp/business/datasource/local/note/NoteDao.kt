package com.razzaghi.noteapp.business.datasource.local.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.razzaghi.noteapp.business.datasource.local.note.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM Note WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("SELECT * FROM Note ORDER BY id DESC")
    suspend fun getAllNotes(): List<NoteEntity>


    @Query("SELECT * FROM Note WHERE id = :noteId")
    suspend fun getNote(noteId: Long): NoteEntity
}
