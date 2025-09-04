package com.razzaghi.noteapp.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.database.Cursor

fun EventData.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = startMillis
    }
}

fun EventData.toEndCalendar(): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = endMillis
    }
}
fun getEventById(context: Context, eventId: Long?): EventData? {
    val projection = arrayOf(
        CalendarContract.Events._ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DESCRIPTION,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.EVENT_LOCATION
    )

    val cursor: Cursor? = context.contentResolver.query(
        CalendarContract.Events.CONTENT_URI,
        projection,
        "${CalendarContract.Events._ID} = ?",
        arrayOf(eventId.toString()),
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val id = it.getLong(0)
            val title = it.getString(1)
            val description = it.getString(2)
            val start = it.getLong(3)
            val end = it.getLong(4)
            val location = it.getString(5)

            return EventData(id, title, description, start, end, location)
        }
    }
    return null
}

data class EventData(
    val id: Long,
    val title: String?,
    val description: String?,
    val startMillis: Long,
    val endMillis: Long,
    val location: String?
)


fun insertEvent(
    context: Context,
    calendar: Calendar,
    title: String,
    description: String = "",
    location: String = ""
): Long? {
    val cr = context.contentResolver

    // Get the first available calendar ID
    val cursor = cr.query(
        CalendarContract.Calendars.CONTENT_URI,
        arrayOf(CalendarContract.Calendars._ID),
        null,
        null,
        null
    )

    val calendarId = cursor?.use {
        if (it.moveToFirst()) it.getLong(0) else null
    } ?: return null

    val startMillis = calendar.timeInMillis
    val endMillis = startMillis + 60 * 60 * 1000 // 1 hour later

    val values = ContentValues().apply {
        put(CalendarContract.Events.DTSTART, startMillis)
        put(CalendarContract.Events.DTEND, endMillis)
        put(CalendarContract.Events.TITLE, title)
        put(CalendarContract.Events.DESCRIPTION, description)
        put(CalendarContract.Events.CALENDAR_ID, calendarId)
        put(CalendarContract.Events.EVENT_TIMEZONE, calendar.timeZone.id)
        put(CalendarContract.Events.EVENT_LOCATION, location)
    }

    val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
    return uri?.lastPathSegment?.toLong()
}


fun Calendar.toFriendlyString(): String {
    val now = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())

    return when {
        this.isSameDay(now) -> "Today, ${timeFormat.format(time)}"
        this.isSameDay(tomorrow) -> "Tomorrow, ${timeFormat.format(time)}"
        else -> "${dateFormat.format(time)}, ${timeFormat.format(time)}"
    }
}

fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}
