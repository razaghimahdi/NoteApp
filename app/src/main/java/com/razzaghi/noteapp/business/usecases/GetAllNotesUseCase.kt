package com.razzaghi.noteapp.business.usecases

import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.ProgressBarState
import com.razzaghi.noteapp.business.datasource.local.note.NoteDao
import com.razzaghi.noteapp.business.datasource.local.note.entity.toNote
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.business.util.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetAllNotesUseCase(
    private val cache: NoteDao,
) {


    fun execute(): Flow<DataState<List<Note>>> = flow {

        try {

            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))


            val result = cache.getAllNotes()


            emit(DataState.Data(result.map { it.toNote() }))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(handleUseCaseException(e))

        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }


    }


}