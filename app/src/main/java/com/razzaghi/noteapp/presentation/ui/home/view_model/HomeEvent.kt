package com.razzaghi.noteapp.presentation.ui.home.view_model

import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.ViewEvent

sealed class HomeEvent : ViewEvent {

    data class OnUpdateQuery(val query: String) : HomeEvent()

    data object ChangeListType : HomeEvent()

    data object OnRetryNetwork : HomeEvent()

    data class OnUpdateNetworkState(val networkState: NetworkState) : HomeEvent()
}
