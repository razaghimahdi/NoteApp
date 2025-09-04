package com.razzaghi.noteapp.presentation.ui.edit.view_model

import com.razzaghi.noteapp.business.core.ViewSingleAction


sealed class EditAction : ViewSingleAction {

    sealed class Navigation : EditAction() {
        data object PopUp : Navigation()
    }

}