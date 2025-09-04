package com.razzaghi.noteapp.presentation.ui.edit.view_model

import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.business.core.ViewEvent
import java.util.Calendar

sealed class EditEvent : ViewEvent {

    data object DeleteNote  : EditEvent()

    data class GetNote(val id: Long) : EditEvent()

    data class AddReminder(val calendar: Calendar?) : EditEvent()

    data class OnUpdateTitle(val value: String) : EditEvent()

    data class OnUpdateNote(val value: String) : EditEvent()

    data class OnUpdateLabel(val value: String) : EditEvent()

    data class OnUpdateEventId(val value: Long) : EditEvent()

    data class OnUpdateId(val value: Long) : EditEvent()

    data class OnUpdateNotificationBottomSheet(val value: UIComponentState) : EditEvent()

    data class OnUpdateReminderDialog(val value: UIComponentState) : EditEvent()

    data class UpdateNote(val eventId: Long?) : EditEvent()

    data object OnRetryNetwork : EditEvent()

    data class OnUpdateNetworkState(val networkState: NetworkState) : EditEvent()
}
