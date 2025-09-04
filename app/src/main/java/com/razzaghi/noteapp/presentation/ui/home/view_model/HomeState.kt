package com.razzaghi.noteapp.presentation.ui.home.view_model

import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.ViewState
import com.razzaghi.noteapp.business.domain.Note

data class HomeState(
    val listType:Int = 0,
    val query:String = "",
    val notes: List<Note> = listOf(),
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
) : ViewState
