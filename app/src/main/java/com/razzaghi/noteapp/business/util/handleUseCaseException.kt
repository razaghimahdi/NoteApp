package com.razzaghi.noteapp.business.util


import android.annotation.SuppressLint
import com.razzaghi.noteapp.business.core.DataState
import com.razzaghi.noteapp.business.core.UIComponent


private val TAG = "AppDebug handleUseCaseException"

@SuppressLint("LongLogTag")
fun <T> handleUseCaseException(
    e: Throwable,
): DataState.Response<T> {

    e.printStackTrace()



    e.message?.let {


        return when (it) {


            ErrorHandling.GENERAL_ERROR -> {
                DataState.Response<T>(
                    uiComponent = UIComponent.DialogSimple(
                        title = "",
                        description = ErrorHandling.GENERAL_ERROR
                    )
                )
            }

            else -> {
                DataState.Response<T>(
                    uiComponent = UIComponent.DialogSimple(
                        title = "",
                        description = ErrorHandling.GENERAL_ERROR
                    )
                )
            }


        }
    }

    return DataState.Response<T>(
        uiComponent = UIComponent.DialogSimple(
            title = "Error",
            description = e.message ?: "Unknown error"
        )
    )
}