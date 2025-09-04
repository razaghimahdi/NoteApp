package com.razzaghi.noteapp.presentation.ui.add.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.razzaghi.noteapp.R
import com.razzaghi.noteapp.presentation.ui.theme.NoteAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(onDismiss: () -> Unit) {


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(4.dp),
    ) {
        Content()
    }
}

@Composable
private fun Content() {
    Column(modifier = Modifier.fillMaxWidth()) {
        NotifBox(icon = R.drawable.ic_clock, label = R.string.later_today, desc = "6:30")
        NotifBox(icon = R.drawable.ic_clock, label = R.string.tomorrow_morning, desc = "6:30")
        NotifBox(icon = R.drawable.ic_home, label = R.string.home, desc = "Tehran")
        NotifBox(icon = R.drawable.ic_calendar, label = R.string.pick_date, secondaryIcon = R.drawable.ic_add, divider = false)
    }
}

@Composable
fun NotifBox(icon: Int, label: Int, desc: String = "", secondaryIcon: Int? = null, divider: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Image(painterResource(icon), null)
            VerticalDivider(modifier = Modifier.height(12.dp))
            Text(stringResource(label), style = MaterialTheme.typography.bodyMedium)
        }
        if (secondaryIcon != null) {
            Icon(painterResource(secondaryIcon), null, tint = Color.Black)
        } else {
            Text(desc, style = MaterialTheme.typography.bodyLarge)
        }
    }
    if (divider) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}


@Preview
@Composable
private fun NotificationBottomSheetPreview() {
    NoteAppTheme {
        Surface {
            Content()
        }
    }
}
