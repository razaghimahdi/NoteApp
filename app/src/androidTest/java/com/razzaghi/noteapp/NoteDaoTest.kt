package com.razzaghi.noteapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.razzaghi.noteapp.business.datasource.local.AppDatabase
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.NoteEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

/**
 * Integration tests for the NoteDao.
 * This class tests the interaction between the DAO and a real in-memory Room database.
 */
@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: NoteDao

    /**
     * This function is executed before each test.
     * It creates a new in-memory database, which is temporary and exists only for the test run.
     * This ensures that each test starts with a clean slate and is not affected by other tests.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using an in-memory database ensures that the database is cleared after the process dies.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        dao = db.noteDao()
    }

    /**
     * This function is executed after each test.
     * It closes the database to clean up resources.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * This is a full CRUD (Create, Read, Update, Delete) cycle test.
     * It verifies that all DAO operations work together as expected.
     */
    @Test
    @Throws(Exception::class)
    fun insertNote_getNote_updateNote_deleteNote_Cycle_Successfully() = runTest {
        // 1. ARRANGE: Create a note entity to insert.
        val noteToInsert = NoteEntity(id = 1, title = "First Note", note = "Content", createdAt = Date().time, location = "Test Location", label = "Test Label")

        // 2. ACT & ASSERT (CREATE): Insert the note and verify it's there.
        dao.insertNote(noteToInsert)
        var allNotes = dao.getAllNotes()
        assertThat(allNotes).hasSize(1)
        assertThat(allNotes[0]).isEqualTo(noteToInsert)

        // 3. ACT & ASSERT (READ): Get the specific note by ID and verify its details.
        val retrievedNote = dao.getNote(1L)
        assertThat(retrievedNote).isNotNull()
        assertThat(retrievedNote.title).isEqualTo("First Note")

        // 4. ARRANGE (UPDATE): Create an updated version of the note.
        val noteToUpdate = NoteEntity(id = 1, title = "Updated Title", note = "Updated Content", createdAt = noteToInsert.createdAt, location = "New Location", label = "New Label")

        // 5. ACT & ASSERT (UPDATE): Update the note and verify the changes.
        dao.updateNote(noteToUpdate)
        val updatedNote = dao.getNote(1L)
        assertThat(updatedNote).isNotNull()
        assertThat(updatedNote.title).isEqualTo("Updated Title")
        assertThat(updatedNote.note).isEqualTo("Updated Content")

        // 6. ACT & ASSERT (DELETE): Delete the note and verify the database is empty.
        dao.deleteNote(1L)
        allNotes = dao.getAllNotes()
        assertThat(allNotes).isEmpty()
    }
}