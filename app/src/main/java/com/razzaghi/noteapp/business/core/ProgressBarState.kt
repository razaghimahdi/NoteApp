package com.razzaghi.noteapp.business.core

sealed class ProgressBarState{

   data object Loading: ProgressBarState()

   data object Idle: ProgressBarState()

}

