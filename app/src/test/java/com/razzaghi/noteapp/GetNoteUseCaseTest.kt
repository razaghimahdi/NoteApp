package com.razzaghi.noteapp


import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.NoteEntity
import com.razzaghi.noteapp.business.datasource.local.note.entity.toEntity
import com.razzaghi.noteapp.business.datasource.local.note.entity.toNote
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.AddNoteUseCase
import com.razzaghi.noteapp.business.usecases.GetNoteUseCase
import com.razzaghi.noteapp.business.util.ErrorHandling
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class GetNoteUseCaseTest {

    @RelaxedMockK
    private lateinit var noteDao: NoteDao

    private lateinit var getNoteUseCase: GetNoteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getNoteUseCase = GetNoteUseCase(cache = noteDao)
    }

    /**
     * Test case for the success path.
     * Verifies that the use case emits Loading, then Data with the correct note,
     * and finally Idle when the DAO finds the note.
     */
    @Test
    fun `execute WHEN note is found THEN emit success data state`() = runTest {
        // ARRANGE
        val noteIdToFind = 1L
        val mockNoteEntity = NoteEntity(id = noteIdToFind, title = "Found Note", note = "Content", createdAt = Date().time, location = "", label = "")
        val expectedNote = mockNoteEntity.toNote()

        // Mock the DAO to return the specific note entity
        coEvery { noteDao.getNote(noteIdToFind) } returns mockNoteEntity

        // ACT
        val emissions = getNoteUseCase.execute(noteIdToFind).toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Data, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)

        // 2. Check Data state
        assertTrue(emissions[1] is DataState.Data)
        val data = (emissions[1] as DataState.Data<Note>).data
        assertEquals(expectedNote, data)
        assertEquals("Found Note", data?.title)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)

        // 4. (Optional) Verify that getNote was called
        coVerify(exactly = 1) { noteDao.getNote(noteIdToFind) }
    }

    /**
     * Test case for the failure path.
     * Verifies that the use case emits a Response (error) state when the DAO
     * throws an exception (e.g., note not found).
     */
    @Test
    fun `execute WHEN dao throws exception THEN emit error response state`() = runTest {
        // ARRANGE
        val noteIdToFind = 999L // An ID that won't be found
        val exception = RuntimeException("Note with ID $noteIdToFind not found in database")
        // Mock the DAO to throw an error
        coEvery { noteDao.getNote(noteIdToFind) } throws exception

        // ACT
        val emissions = getNoteUseCase.execute(noteIdToFind).toList()

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