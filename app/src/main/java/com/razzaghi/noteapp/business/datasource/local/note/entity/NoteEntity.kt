package com.razzaghi.noteapp.business.datasource.local.note.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.razzaghi.noteapp.business.domain.Note
import java.util.Date

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val note: String,
    val eventId: Long? = null,
    val createdAt: Long = Date().time,
    val location: String,
    val label: String,
)


fun NoteEntity.toNote() = Note(
    id = id,
    title = title,
    note = note,
    createdAt = createdAt,
    location = location,
    label = label,
    eventId = eventId,
)

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    note = note,
    createdAt = createdAt,
    location = location,
    label = label,
    eventId = eventId,
)