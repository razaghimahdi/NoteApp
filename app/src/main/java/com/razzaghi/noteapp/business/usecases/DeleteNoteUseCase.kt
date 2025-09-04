package com.razzaghi.noteapp.business.usecases

import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DeleteNoteUseCase(
    private val cache: NoteDao,
) {


    fun execute(id: Long): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))


            cache.deleteNote(noteId = id)


            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }


}