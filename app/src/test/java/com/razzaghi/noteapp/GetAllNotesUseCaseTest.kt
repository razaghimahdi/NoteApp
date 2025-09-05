import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.NoteEntity
import com.razzaghi.noteapp.business.datasource.local.note.entity.toNote
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.GetAllNotesUseCase
import com.razzaghi.noteapp.business.util.ErrorHandling
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.MockKAnnotations
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Unit tests for the [GetAllNotesUseCase].
 */
class GetAllNotesUseCaseTest {

    @RelaxedMockK
    private lateinit var noteDao: NoteDao

    private lateinit var getAllNotesUseCase: GetAllNotesUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getAllNotesUseCase = GetAllNotesUseCase(cache = noteDao)
    }

    /**
     * Test case for the success path.
     * Verifies that the use case emits Loading, then Data, and finally Idle states
     * when the DAO successfully returns a list of notes.
     */
    @Test
    fun `execute WHEN dao returns notes THEN emit success data state`() = runTest {
        // ARRANGE
        val mockNoteEntities = listOf(
            NoteEntity(id = 1, title = "Note 1", note = "Content 1", createdAt = Date().time, location = "Location 1", label = "Label 1"),
            NoteEntity(id = 2, title = "Note 2", note = "Content 2", createdAt = Date().time, location = "Location 2", label = "Label 2")
        )
        val expectedNotes = mockNoteEntities.map { it.toNote() }
        coEvery { noteDao.getAllNotes() } returns mockNoteEntities

        // ACT
        val emissions = getAllNotesUseCase.execute().toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Data, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)
        assertEquals(ProgressBarState.Loading, (emissions[0] as DataState.Loading).progressBarState)

        // 2. Check Data state
        assertTrue(emissions[1] is DataState.Data)
        val data = (emissions[1] as DataState.Data<List<Note>>).data
        assertEquals(expectedNotes, data)
        assertEquals(2, data?.size)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)
    }

    @Test
    fun `execute WHEN dao throws exception THEN emit generic error response`() = runTest {
        // ARRANGE
        // The exception message is not null, so handleUseCaseException should return the GENERAL_ERROR
        val exception = RuntimeException("Database connection lost")
        coEvery { noteDao.getAllNotes() } throws exception

        // ACT
        val emissions = getAllNotesUseCase.execute().toList()

        // ASSERT
        assertEquals(3, emissions.size) // Expect: Loading, Response, Idle

        // 1. Check Loading state
        assertTrue(emissions[0] is DataState.Loading)
        assertEquals(ProgressBarState.Loading, (emissions[0] as DataState.Loading).progressBarState)

        // 2. Check Response state (for the error)
        assertTrue(emissions[1] is DataState.Response)
        val uiComponent = (emissions[1] as DataState.Response).uiComponent

        // **Assert that the correct UIComponent type is used**
        assertTrue(uiComponent is UIComponent.DialogSimple)

        // **Assert that the description is the generic error message, as per your function's logic**
        val description = (uiComponent as UIComponent.DialogSimple).description
        assertEquals(ErrorHandling.GENERAL_ERROR, description)

        // 3. Check Idle state
        assertTrue(emissions[2] is DataState.Loading)
        assertEquals(ProgressBarState.Idle, (emissions[2] as DataState.Loading).progressBarState)
    }
}