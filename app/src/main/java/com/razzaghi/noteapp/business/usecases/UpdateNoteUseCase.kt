package com.razzaghi.noteapp.business.usecases

import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.toEntity
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class UpdateNoteUseCase(
    private val cache: NoteDao,
) {


    fun execute(note: Note): Flow<DataState<Boolean>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            cache.updateNote(note = note.toEntity())

            emit(DataState.Data(true))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }


}