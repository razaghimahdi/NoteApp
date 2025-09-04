package com.razzaghi.noteapp.presentation.navigation

import com.razzaghi.noteapp.business.domain.Note
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavigation {


    @Serializable
    data object Home : MainNavigation

    @Serializable
    data object AddNote : MainNavigation

    @Serializable
    data class EditNote(val noteId: Long) : MainNavigation

}