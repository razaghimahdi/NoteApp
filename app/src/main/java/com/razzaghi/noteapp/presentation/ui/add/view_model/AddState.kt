package com.razzaghi.noteapp.presentation.ui.add.view_model

import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.business.core.ViewState
import java.util.Calendar

data class AddState(
    val reminder: Calendar? = null,
    val title:String = "",
    val note:String = "",
    val location:String = "Tehran",
    val label:String = "",
    val notificationBottomSheet: UIComponentState = UIComponentState.Hide,
    val reminderDialog: UIComponentState = UIComponentState.Hide,
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val networkState: NetworkState = NetworkState.Good,
) : ViewState
