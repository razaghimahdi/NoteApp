package com.razzaghi.noteapp

import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.toEntity
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.AddNoteUseCase
import com.razzaghi.noteapp.business.util.ErrorHandling
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class AddNoteUseCaseTest {

    @RelaxedMockK
    private lateinit var noteDao: NoteDao

    private lateinit var addNoteUseCase: AddNoteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        addNoteUseCase = AddNoteUseCase(cache = noteDao)
    }

    /**
     * Test case for the success path.
     * Verifies that the use case emits Loading, then Data(true), and finally Idle
     * when the DAO successfully inserts the note.
     */
    @Test
    fun `execute WHEN note is inserted successfully THEN emit success data state`() = runTest {
        // ARRANGE
        val newNote = Note(id = 1, title = "Test Note", note = "Content", createdAt = Date().time, location = "", label = "")
        // Mock the insert function to do nothing and return successfully (Unit)
        coEvery { noteDao.insertNote(any()) } returns Unit

        // ACT
        val emissions = addNoteUseCase.execute(newNote).toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Data, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)
        assertEquals(ProgressBarState.Loading, (emissions[0] as DataState.Loading).progressBarState)

        // 2. Check Data state
        assertTrue(emissions[1] is DataState.Data)
        assertEquals(true, (emissions[1] as DataState.Data<Boolean>).data)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)

        // 4. (Optional) Verify that insertNote was actually called once
        coVerify(exactly = 1) { noteDao.insertNote(newNote.toEntity()) }
    }

    /**
     * Test case for the failure path.
     * Verifies that the use case emits a Response (error) state when the DAO
     * throws an exception during insertion.
     */
    @Test
    fun `execute WHEN dao throws exception THEN emit error response state`() = runTest {
        // ARRANGE
        val newNote = Note(id = 1, title = "Test Note", note = "Content", createdAt = Date().time, location = "", label = "")
        val exception = RuntimeException("Failed to write to database")
        // Mock the insert function to throw an error
        coEvery { noteDao.insertNote(any()) } throws exception

        // ACT
        val emissions = addNoteUseCase.execute(newNote).toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Response, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)

        // 2. Check Response state (for the error)
        assertTrue(emissions[1] is DataState.Response)
        val uiComponent = (emissions[1] as DataState.Response).uiComponent
        assertTrue(uiComponent is UIComponent.DialogSimple)
        assertEquals(ErrorHandling.GENERAL_ERROR, (uiComponent as UIComponent.DialogSimple).description)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)
    }
}