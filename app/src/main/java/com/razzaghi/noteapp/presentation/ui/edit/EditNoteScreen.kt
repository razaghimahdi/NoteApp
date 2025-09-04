package com.razzaghi.noteapp.presentation.ui.edit

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import com.razzaghi.noteapp.presentation.components.noRippleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.razzaghi.noteapp.R
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.core.UIComponentState
import com.razzaghi.noteapp.presentation.components.CircleBorderButton
import com.razzaghi.noteapp.presentation.components.CircleButton
import com.razzaghi.noteapp.presentation.components.DefaultScreenUI
import com.razzaghi.noteapp.presentation.components.IconTextChips
import com.razzaghi.noteapp.presentation.components.TextChips
import com.razzaghi.noteapp.presentation.ui.add.components.NotificationBottomSheet
import com.razzaghi.noteapp.presentation.ui.add.components.ReminderDialog
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditAction
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditEvent
import com.razzaghi.noteapp.presentation.ui.edit.view_model.EditState
import com.razzaghi.noteapp.presentation.ui.theme.NoteAppTheme
import com.razzaghi.noteapp.presentation.ui.theme.TextFieldWithTransparentTheme
import com.razzaghi.noteapp.presentation.util.getEventById
import com.razzaghi.noteapp.presentation.util.insertEvent
import com.razzaghi.noteapp.presentation.util.toCalendar
import com.razzaghi.noteapp.presentation.util.toFriendlyString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    errors: Flow<UIComponent>,
    action: Flow<EditAction>,
    state: EditState,
    events: (EditEvent) -> Unit,
    popUp: (refresh: Boolean) -> Unit
) {


    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(key1 = action) {
        action.onEach { effect ->
            when (effect) {
                EditAction.Navigation.PopUp -> {
                    popUp(true)
                }
            }
        }.collect {}
    }


    LaunchedEffect(state.eventId) {
        events(EditEvent.AddReminder(getEventById(context, state.eventId)?.toCalendar()))
    }

    if (state.notificationBottomSheet == UIComponentState.Show) {
        NotificationBottomSheet(onDismiss = { events(EditEvent.OnUpdateNotificationBottomSheet(UIComponentState.Hide)) })
    }


    if (state.reminderDialog == UIComponentState.Show) {
        ReminderDialog(onDismiss = { events(EditEvent.OnUpdateReminderDialog(UIComponentState.Hide)) }) { calendar ->
            events(EditEvent.AddReminder(calendar))
        }
    }




    DefaultScreenUI(
        errors = errors,
        progressBarState = state.progressBarState,
        networkState = state.networkState,
        topBar = {
            EditNoteToolbar({ popUp(false) }, {
                events(EditEvent.OnUpdateReminderDialog(UIComponentState.Show))
            })
        },
        bottomBar = {
            EditNoteBottomBar(enabled = state.title.isNotEmpty(), onAddExecute = {
                if (state.reminder == null) {
                    events(EditEvent.UpdateNote(null))
                } else {
                    if (hasPermission) {
                        val id = insertEvent(
                            context,
                            state.reminder,
                            title = state.title,
                            description = state.note,
                            location = state.location
                        )
                        events(EditEvent.UpdateNote(eventId = id))
                    } else {
                        launcher.launch(Manifest.permission.WRITE_CALENDAR)
                    }
                }
            }, onLabel = {
                events(EditEvent.OnUpdateLabel("Work"))
            }, onDeleteExecute = {
                events(EditEvent.DeleteNote)
            })
        },
        onTryAgain = { events(EditEvent.OnRetryNetwork) },
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimatedVisibility(state.label.isNotEmpty()) {
                    TextChips(state.label) {
                        events(EditEvent.OnUpdateLabel(""))
                    }
                }
                AnimatedVisibility(state.reminder != null) {
                    IconTextChips(label = "${state.reminder?.toFriendlyString()}", icon = R.drawable.ic_timer) {
                        events(EditEvent.AddReminder(null))
                    }
                }
            }


            TextField(
                placeholder = {
                    state.title.ifEmpty {
                        Text(
                            stringResource(R.string.title),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                value = state.title,
                onValueChange = {
                    events(EditEvent.OnUpdateTitle(it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default,
                    keyboardType = KeyboardType.Text,
                ),
                maxLines = 1,
                colors = TextFieldWithTransparentTheme(),
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            TextField(
                placeholder = {
                    state.note.ifEmpty {
                        Text(
                            stringResource(R.string.note),
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                value = state.note,
                onValueChange = {
                    events(EditEvent.OnUpdateNote(it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                ),
                colors = TextFieldWithTransparentTheme(),
                modifier = Modifier.fillMaxSize()
            )


        }
    }
}

@Composable
fun EditNoteBottomBar(enabled: Boolean, onAddExecute: () -> Unit, onDeleteExecute: () -> Unit, onLabel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.size(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {

            Row(modifier = Modifier.noRippleClickable { onLabel() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painterResource(R.drawable.ic_tag), null)
                Text(stringResource(R.string.labels), style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.size(16.dp))
            CircleButton(R.drawable.ic_delete, containerColor = Color.Red, onClick = onDeleteExecute)
            Spacer(modifier = Modifier.size(8.dp))
            CircleButton(R.drawable.ic_done, enabled = enabled, onClick = onAddExecute)
        }

        Spacer(modifier = Modifier.size(24.dp))

    }
}

@Composable
fun EditNoteToolbar(onBack: () -> Unit, onNotif: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircleBorderButton(icon = R.drawable.ic_left_arrow, onClick = onBack)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CircleBorderButton(icon = R.drawable.ic_add_notif, onClick = onNotif)
            CircleBorderButton(icon = R.drawable.ic_draft, onClick = onBack)
        }
    }
}

@Preview
@Composable
private fun HomeScreenHome() {
    NoteAppTheme {
        EditNoteScreen(emptyFlow(), emptyFlow(), EditState(), {}, {})
    }
}