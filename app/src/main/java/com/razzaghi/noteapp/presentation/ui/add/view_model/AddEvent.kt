package com.razzaghi.noteapp.presentation.ui.add.view_model

import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.business.core.ViewEvent
import java.util.Calendar

sealed class AddEvent : ViewEvent {

    data class AddReminder(val calendar: Calendar?) : AddEvent()

    data class OnUpdateTitle(val value: String) : AddEvent()

    data class OnUpdateNote(val value: String) : AddEvent()

    data class OnUpdateLabel(val value: String) : AddEvent()

    data class OnUpdateNotificationBottomSheet(val value: UIComponentState) : AddEvent()

    data class OnUpdateReminderDialog(val value: UIComponentState) : AddEvent()

    data class AddNote(val eventId: Long?) : AddEvent()

    data object OnRetryNetwork : AddEvent()

    data class OnUpdateNetworkState(val networkState: NetworkState) : AddEvent()
}
