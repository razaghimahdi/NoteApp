package com.razzaghi.noteapp.presentation


import android.app.Application
import com.razzaghi.noteapp.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApplications : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BaseApplications)
            modules(listOf(AppModule))
        }

    }
}