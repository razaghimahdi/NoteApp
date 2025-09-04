package com.razzaghi.noteapp.presentation.ui.add.view_model

import androidx.lifecycle.viewModelScope
import com.razzaghi.noteapp.business.core.BaseViewModel
import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.usecases.AddNoteUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Calendar
import kotlin.let

class AddNoteViewModel(
    private val addNoteUseCase: AddNoteUseCase
) : BaseViewModel<AddEvent, AddState, AddAction>() {

    override fun setInitialState() = AddState()

    override fun onTriggerEvent(event: AddEvent) {
        when (event) {

            is AddEvent.AddReminder -> {
                onAddReminder(calendar = event.calendar)
            }

            is AddEvent.AddNote -> {
                onAddNote(eventId =event.eventId)
            }

            is AddEvent.OnUpdateNote -> {
                onUpdateNote(value = event.value)
            }

            is AddEvent.OnUpdateTitle -> {
                onUpdateTitle(value = event.value)
            }


            is AddEvent.OnUpdateLabel -> {
                onUpdateLabel(value = event.value)
            }


            is AddEvent.OnUpdateReminderDialog -> {
                onUpdateReminderDialog(value = event.value)
            }


            is AddEvent.OnUpdateNotificationBottomSheet -> {
                onUpdateNotificationBottomSheet(value = event.value)
            }

            is AddEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is AddEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
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


    private fun onAddNote(eventId: Long?) {
        addNoteUseCase.execute(note = Note(title = state.value.title,eventId = eventId, note = state.value.note, label = state.value.label, location = state.value.location)).onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(AddEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    setError { dataState.uiComponent }
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        setAction{ AddAction.Navigation.PopUp }
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