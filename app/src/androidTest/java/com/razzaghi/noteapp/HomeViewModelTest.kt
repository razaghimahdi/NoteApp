package com.razzaghi.noteapp

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.GetAllNotesUseCase
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeEvent
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @RelaxedMockK
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    /**
     * Verifies the *final state* after a successful data fetch in the `init` block.
     */
    @Test
    fun init_whenNotesFetchIsSuccessful_thenFinalStateIsCorrect() = runTest {
        // ARRANGE
        val mockNotes = listOf(Note(id = 1, title = "Test Note", note = "Content", createdAt = Date().time, location = "", label = ""))
        val successFlow = flowOf(
            DataState.Loading(progressBarState = ProgressBarState.Loading),
            DataState.Data(mockNotes),
            DataState.Loading(progressBarState = ProgressBarState.Idle)
        )
        every { getAllNotesUseCase.execute() } returns successFlow

        // ACT
        viewModel = HomeViewModel(getAllNotesUseCase)

        // ASSERT
        // runTest ensures the init block's coroutine completes before this line.
        val finalState = viewModel.state.value
        assertThat(finalState.notes).isEqualTo(mockNotes)
        assertThat(finalState.progressBarState).isEqualTo(ProgressBarState.Idle)
    }

    /**
     * Verifies that the 'errors' channel receives an error when the use case fails.
     */
    @Test
    fun init_whenNotesFetchFails_sendsErrorToErrorsChannel() = runTest {
        // ARRANGE
        val errorComponent = UIComponent.DialogSimple(title = "Error", description = "Database error")

        // The FIX is here: We explicitly define the Flow's type to help the compiler.
        val errorFlow: Flow<DataState<List<Note>>> = flowOf(
            DataState.Response(errorComponent)
        )

        every { getAllNotesUseCase.execute() } returns errorFlow
        viewModel = HomeViewModel(getAllNotesUseCase)

        // ACT & ASSERT on the `errors` Flow
        viewModel.errors.test {
            // Assert that the correct error component was received
            assertThat(awaitItem()).isEqualTo(errorComponent)
            cancelAndIgnoreRemainingEvents() // Best practice to ensure no other events
        }
    }

    /**
     * Verifies that the ChangeListType event correctly toggles the listType state.
     */
    @Test
    fun setEvent_changeListType_togglesListTypeState() = runTest {
        // ARRANGE
        every { getAllNotesUseCase.execute() } returns flowOf() // for the init block
        viewModel = HomeViewModel(getAllNotesUseCase)

        // ASSERT initial state
        assertThat(viewModel.state.value.listType).isEqualTo(0)

        // ACT 1
        viewModel.setEvent(HomeEvent.ChangeListType)
        // ASSERT 1
        assertThat(viewModel.state.value.listType).isEqualTo(1)

        // ACT 2
        viewModel.setEvent(HomeEvent.ChangeListType)
        // ASSERT 2
        assertThat(viewModel.state.value.listType).isEqualTo(0)
    }

    /**
     * Verifies that the OnUpdateQuery event correctly updates the query state.
     */
    @Test
    fun setEvent_onUpdateQuery_updatesQueryState() = runTest {
        // ARRANGE
        every { getAllNotesUseCase.execute() } returns flowOf() // for the init block
        viewModel = HomeViewModel(getAllNotesUseCase)
        val testQuery = "search for this"

        // ASSERT initial state
        assertThat(viewModel.state.value.query).isEqualTo("")

        // ACT
        viewModel.setEvent(HomeEvent.OnUpdateQuery(testQuery))

        // ASSERT
        assertThat(viewModel.state.value.query).isEqualTo(testQuery)
    }
}