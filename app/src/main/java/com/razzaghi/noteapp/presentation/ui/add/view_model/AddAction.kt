package com.razzaghi.noteapp.presentation.ui.add.view_model

import com.razzaghi.noteapp.business.core.ViewSingleAction


sealed class AddAction : ViewSingleAction {

    sealed class Navigation : AddAction() {
        data object PopUp : Navigation()
    }

}