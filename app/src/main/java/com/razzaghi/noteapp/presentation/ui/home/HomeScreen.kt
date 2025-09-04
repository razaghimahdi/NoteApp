package com.razzaghi.noteapp.presentation.ui.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.razzaghi.noteapp.presentation.components.noRippleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.razzaghi.noteapp.R
import com.razzaghi.noteapp.business.core.UIComponent
import com.razzaghi.noteapp.business.domain.Note
import com.razzaghi.noteapp.presentation.components.CircleButton
import com.razzaghi.noteapp.presentation.components.DefaultScreenUI
import com.razzaghi.noteapp.presentation.components.IconTextChips
import com.razzaghi.noteapp.presentation.components.TextChips
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeEvent
import com.razzaghi.noteapp.presentation.ui.home.view_model.HomeState
import com.razzaghi.noteapp.presentation.ui.theme.NoteAppTheme
import com.razzaghi.noteapp.presentation.util.getEventById
import com.razzaghi.noteapp.presentation.util.toCalendar
import com.razzaghi.noteapp.presentation.util.toFriendlyString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun HomeScreen(errors: Flow<UIComponent>, state: HomeState, events: (HomeEvent) -> Unit, navigateToAdd: () -> Unit, navigateToEdit: (Note) -> Unit) {
    val context = LocalContext.current

    DefaultScreenUI(
        errors = errors,
        progressBarState = state.progressBarState,
        networkState = state.networkState,
        topBar = {
            HomeToolbar(listType = state.listType) {
                events(HomeEvent.ChangeListType)
            }
        },
        bottomBar = {
            HomeBottomBar {
                navigateToAdd()
            }
        },
        onTryAgain = { events(HomeEvent.OnRetryNetwork) },
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.recent_notes), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.size(32.dp))

            if (state.listType == 0) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.notes) {
                        NoteBox(note = it, context = context) {
                            navigateToEdit(it)
                        }
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(modifier = Modifier.fillMaxWidth(), columns = StaggeredGridCells.Fixed(2)) {
                    items(state.notes) {
                        NoteBoxGrid(note = it, context = context) {
                            navigateToEdit(it)
                        }
                    }
                }
            }

        }

    }
}

@Composable
fun HomeBottomBar(onAddExecute: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.size(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {

            Image(painterResource(R.drawable.ic_tag), null)
            Spacer(modifier = Modifier.size(12.dp))
            Text(stringResource(R.string.labels), style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.size(16.dp))
            CircleButton(R.drawable.ic_add, onClick = onAddExecute)
        }

        Spacer(modifier = Modifier.size(24.dp))

    }
}

@Composable
fun NoteBox(note: Note, context: Context, onClick: () -> Unit) {

    val event = getEventById(context = context, eventId = note.eventId)?.toCalendar()

    Column(
        modifier = Modifier
            .noRippleClickable {
                onClick()
            }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(note.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.size(8.dp))
        Text(note.note, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.size(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            if (event != null) {
                IconTextChips(label = event.toFriendlyString(), icon = R.drawable.ic_timer)
            }

            if (note.label.isNotEmpty()) {
                TextChips(note.label)
            }

        }

    }

    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
fun NoteBoxGrid(note: Note, context: Context, onClick: () -> Unit) {
    val event = getEventById(context = context, eventId = note.eventId)?.toCalendar()

    Column(
        modifier = Modifier
            .noRippleClickable {
                onClick()
            }
            .padding(8.dp)
            .fillMaxWidth(.5f)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(note.title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.size(8.dp))
        Text(note.note, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier) {

            if (event != null) {
                IconTextChips(label = event.toFriendlyString(), icon = R.drawable.ic_timer)
            }
            if (note.label.isNotEmpty()) {
                Spacer(modifier = Modifier.size(8.dp))
                TextChips(note.label)
            }

        }

    }

}


@Composable
fun HomeToolbar(listType: Int, onListClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.fillMaxWidth(.7f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(R.drawable.avatar), contentDescription = null, modifier = Modifier.size(40.dp))
                VerticalDivider(modifier = Modifier.height(16.dp))
                Image(painter = painterResource(R.drawable.ic_search), contentDescription = null, modifier = Modifier.size(24.dp))
                Text(stringResource(R.string.search_your_note), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondary)
            }
            Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(if (listType == 0) R.drawable.ic_grid else R.drawable.ic_list), contentDescription = null, modifier = Modifier
                        .size(20.dp)
                        .noRippleClickable {
                            onListClick()
                        })
                Image(painter = painterResource(R.drawable.ic_menu), contentDescription = null, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Preview
@Composable
private fun HomeScreenHome() {
    NoteAppTheme {
        HomeScreen(
            errors = emptyFlow(),
            state = HomeState(
                listType = 1,
                notes = listOf(
                    Note(
                        id = 1L,
                        title = "Heli Wbsite Design",
                        note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
                        label = "Work"
                    ),
                    Note(
                        id = 2L,
                        title = "Heli Wbsite Design",
                        note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
                    ),
                )
            ),
            events = {},
            navigateToAdd = { },{})
    }
}