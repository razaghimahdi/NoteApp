package com.razzaghi.noteapp.presentation.ui.add.components

import android.widget.TimePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import com.razzaghi.noteapp.presentation.components.noRippleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.razzaghi.noteapp.R
import com.razzaghi.noteapp.presentation.components.CustomAlertDialog
import com.razzaghi.noteapp.presentation.components.DefaultButton
import com.razzaghi.noteapp.presentation.ui.theme.NoteAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialog(onDismiss: () -> Unit = {}, onSave: (calendar: Calendar) -> Unit = { _ -> }) {

    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: "Enter Date"

    CustomAlertDialog(onDismiss) {

        AnimatedVisibility(showDatePicker) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DatePicker(datePickerState)
                Spacer(modifier = Modifier.size(16.dp))
                DefaultButton(text = stringResource(R.string.done)) {
                    showDatePicker = false
                }
            }
        }
        AnimatedVisibility(showTimePicker) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TimePicker(
                    state = timePickerState,
                )
                Spacer(modifier = Modifier.size(16.dp))

                DefaultButton(text = stringResource(R.string.done)) {
                    showTimePicker = false
                }
            }
        }
        AnimatedVisibility(!showTimePicker && !showDatePicker) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(stringResource(R.string.add_reminder), style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.size(16.dp))

                ReminderBox(title = selectedDate) {
                    showDatePicker = true
                }
                ReminderBox(title = "${timePickerState.hour} : ${timePickerState.minute}") {
                    showTimePicker = true
                }
                ReminderBox(title = "Dose not repeat") {}

                Spacer(modifier = Modifier.size(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {

                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                    DefaultButton(
                        text = stringResource(R.string.save), enabled = datePickerState.selectedDateMillis != null
                    ) {
                        val date = Date(datePickerState.selectedDateMillis!!)
                        currentTime.set(date.year, date.month, date.day, timePickerState.hour, timePickerState.minute)
                        onSave(currentTime)
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderBox(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .noRippleClickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Image(painterResource(R.drawable.ic_arrow_down), null)
    }
    HorizontalDivider(modifier = Modifier)
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun NotificationBottomSheetPreview() {
    NoteAppTheme {
        Surface {
            ReminderDialog()
        }
    }
}
