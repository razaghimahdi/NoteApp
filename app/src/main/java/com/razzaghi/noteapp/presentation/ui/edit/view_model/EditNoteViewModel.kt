package com.razzaghi.noteapp.presentation.ui.edit.view_model

import androidx.lifecycle.viewModelScope
import com.razzaghi.noteapp.business.core.BaseViewModel
import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.DeleteNoteUseCase
import com.razzaghi.noteapp.business.usecases.GetNoteUseCase
import com.razzaghi.noteapp.business.usecases.UpdateNoteUseCase
import com.razzaghi.noteapp.presentation.util.getEventById
import com.razzaghi.noteapp.presentation.util.toCalendar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import kotlin.let

class EditNoteViewModel(
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
) : BaseViewModel<EditEvent, EditState, EditAction>() {

    override fun setInitialState() = EditState()

    override fun onTriggerEvent(event: EditEvent) {
        when (event) {

            is EditEvent.GetNote -> {
                onGetNote(id = event.id)
            }

            is EditEvent.DeleteNote -> {
                onDeleteNote()
            }

            is EditEvent.AddReminder -> {
                onAddReminder(calendar = event.calendar)
            }

            is EditEvent.OnUpdateEventId -> {
                onUpdateEventId(value = event.value)
            }

            is EditEvent.OnUpdateId -> {
                onUpdateId(value = event.value)
            }

            is EditEvent.UpdateNote -> {
                onUpdateNote(eventId = event.eventId)
            }

            is EditEvent.OnUpdateNote -> {
                onUpdateNote(value = event.value)
            }

            is EditEvent.OnUpdateTitle -> {
                onUpdateTitle(value = event.value)
            }


            is EditEvent.OnUpdateLabel -> {
                onUpdateLabel(value = event.value)
            }


            is EditEvent.OnUpdateReminderDialog -> {
                onUpdateReminderDialog(value = event.value)
            }


            is EditEvent.OnUpdateNotificationBottomSheet -> {
                onUpdateNotificationBottomSheet(value = event.value)
            }

            is EditEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is EditEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    private fun onUpdateEventId(value: Long) {
        setState { copy(eventId = value) }
    }

    private fun onUpdateId(value: Long) {
        setState { copy(noteId = value) }
    }

    private fun onAddReminder(calendar: Calendar?) {
        setState { copy(reminder = calendar) }
    }

    private fun onUpdateTitle(value: String) {
        setState { copy(title = value) }
    }

    private fun onUpdateNote(value: String) {
        setState { copy(note = value) }
    }

    private fun onUpdateLabel(value: String) {
        setState { copy(label = value) }
    }

    private fun onUpdateReminderDialog(value: UIComponentState) {
        setState { copy(reminderDialog = value) }
    }

    private fun onUpdateNotificationBottomSheet(value: UIComponentState) {
        setState { copy(notificationBottomSheet = value) }
    }

    private fun onGetNote(id: Long) {
        getNoteUseCase.execute(id).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(EditEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    setError { dataState.uiComponent }
                }

                is DataState.Data -> {
                    dataState.data?.let { note ->
                        onTriggerEvent(EditEvent.OnUpdateId(note.id))
                        onTriggerEvent(EditEvent.OnUpdateLabel(note.label))
                        onTriggerEvent(EditEvent.OnUpdateNote(note.note))
                        onTriggerEvent(EditEvent.OnUpdateTitle(note.title))
                        note.eventId?.let {
                            onTriggerEvent(EditEvent.OnUpdateEventId(note.eventId))
                        }
                    }
                }

                is DataState.Loading -> {
                    setState { copy(progressBarState = dataState.progressBarState) }
                }
            }
        }.launchIn(viewModelScope)

    }


    private fun onDeleteNote() {
        deleteNoteUseCase.execute(state.value.noteId).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(EditEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    setError { dataState.uiComponent }
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        setAction { EditAction.Navigation.PopUp }
                    }
                }

                is DataState.Loading -> {
                    setState { copy(progressBarState = dataState.progressBarState) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onUpdateNote(eventId: Long?) {
        updateNoteUseCase.execute(
            note = Note(
                id = state.value.noteId,
                title = state.value.title,
                eventId = eventId,
                note = state.value.note,
                label = state.value.label,
                location = state.value.location
            )
        ).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(EditEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    setError { dataState.uiComponent }
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        setAction { EditAction.Navigation.PopUp }
                    }
                }

                is DataState.Loading -> {
                    setState { copy(progressBarState = dataState.progressBarState) }
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun onRetryNetwork() {

    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        setState { copy(networkState = networkState) }
    }


}