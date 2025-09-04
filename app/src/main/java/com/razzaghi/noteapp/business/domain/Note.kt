package com.razzaghi.noteapp.business.domain



data class Note(
    val id: Long = 0L,
    val title: String = "",
    val note: String = "",
    val eventId: Long? = null,
    val createdAt: Long = 0L,
    val location: String = "",
    val label: String = "",
)