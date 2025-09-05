package com.razzaghi.noteapp


import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.toEntity
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.AddNoteUseCase
import com.razzaghi.noteapp.business.usecases.DeleteNoteUseCase
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

class DeleteNoteUseCaseTest {

    @RelaxedMockK
    private lateinit var noteDao: NoteDao

    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        deleteNoteUseCase = DeleteNoteUseCase(cache = noteDao)
    }

    /**
     * Test case for the success path.
     * Verifies that the use case emits Loading, then Data(true), and finally Idle
     * when the DAO successfully deletes the note.
     */
    @Test
    fun `execute WHEN note is deleted successfully THEN emit success data state`() = runTest {
        // ARRANGE
        val noteIdToDelete = 123L
        // Mock the delete function to do nothing and return successfully (Unit)
        coEvery { noteDao.deleteNote(noteIdToDelete) } returns Unit

        // ACT
        val emissions = deleteNoteUseCase.execute(noteIdToDelete).toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Data, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)

        // 2. Check Data state
        assertTrue(emissions[1] is DataState.Data)
        assertEquals(true, (emissions[1] as DataState.Data<Boolean>).data)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)

        // 4. (Optional) Verify that deleteNote was actually called once with the correct ID
        coVerify(exactly = 1) { noteDao.deleteNote(noteIdToDelete) }
    }

    /**
     * Test case for the failure path.
     * Verifies that the use case emits a Response (error) state when the DAO
     * throws an exception during deletion.
     */
    @Test
    fun `execute WHEN dao throws exception THEN emit error response state`() = runTest {
        // ARRANGE
        val noteIdToDelete = 456L
        val exception = RuntimeException("Failed to access database")
        // Mock the delete function to throw an error
        coEvery { noteDao.deleteNote(noteIdToDelete) } throws exception

        // ACT
        val emissions = deleteNoteUseCase.execute(noteIdToDelete).toList()

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