package com.razzaghi.noteapp.di


import android.content.Context
import androidx.room.Room
import com.razzaghi.noteapp.business.datasource.local.AppDatabase
import com.razzaghi.noteapp.business.usecases.AddNoteUseCase
import com.razzaghi.noteapp.business.usecases.DeleteNoteUseCase
import com.razzaghi.noteapp.business.usecases.GetAllNotesUseCase
import com.razzaghi.noteapp.business.usecases.GetNoteUseCase
import com.razzaghi.noteapp.business.usecases.UpdateNoteUseCase
import com.razzaghi.noteapp.presentation.ui.add.view_model.AddNoteViewModel
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditNoteViewModel
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val AppModule = module {

    single { createAppDatabase(context = androidApplication()) }
    single { get<AppDatabase>().noteDao() }

    single { AddNoteUseCase(get()) }
    single { GetAllNotesUseCase(get()) }
    single { GetNoteUseCase(get()) }
    single { DeleteNoteUseCase(get()) }
    single { UpdateNoteUseCase(get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { AddNoteViewModel(get()) }
    viewModel { EditNoteViewModel(get(), get(), get()) }

}

fun createAppDatabase(context: Context) = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "note_db"
).build()


