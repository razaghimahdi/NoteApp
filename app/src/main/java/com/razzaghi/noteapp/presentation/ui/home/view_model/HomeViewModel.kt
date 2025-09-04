package com.razzaghi.noteapp.presentation.ui.home.view_model

import androidx.lifecycle.viewModelScope
import com.razzaghi.noteapp.business.core.BaseViewModel
import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.NetworkState
import com.razzaghi.noteapp.business.usecases.GetAllNotesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.let

class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
) : BaseViewModel<HomeEvent, HomeState, Nothing>() {

    override fun setInitialState() = HomeState()

    override fun onTriggerEvent(event: HomeEvent) {
        when (event) {

            is HomeEvent.ChangeListType -> {
                onChangeListType()
            }

            is HomeEvent.OnUpdateQuery -> {
                onUpdateQuery(query = event.query)
            }

            is HomeEvent.OnRetryNetwork -> {
                onRetryNetwork()
            }

            is HomeEvent.OnUpdateNetworkState -> {
                onUpdateNetworkState(event.networkState)
            }
        }
    }

    init {
        getNotes()
    }


    private fun onChangeListType() {
        setState { copy(listType = if (state.value.listType == 0) 1 else 0) }

    }

    private fun onUpdateQuery(query: String) {
        setState { copy(query = query) }
    }


    private fun getNotes() {
        getAllNotesUseCase.execute().onEach { dataState ->
            when (dataState) {
                is DataState.NetworkStatus -> {
                    onTriggerEvent(HomeEvent.OnUpdateNetworkState(dataState.networkState))
                }

                is DataState.Response -> {
                    setError { dataState.uiComponent }
                }

                is DataState.Data -> {
                    dataState.data?.let {
                        setState { copy(notes = it) }
                    }
                }

                is DataState.Loading -> {
                    setState { copy(progressBarState = dataState.progressBarState) }
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun onRetryNetwork() {
        getNotes()
    }


    private fun onUpdateNetworkState(networkState: NetworkState) {
        setState { copy(networkState = networkState) }
    }


}