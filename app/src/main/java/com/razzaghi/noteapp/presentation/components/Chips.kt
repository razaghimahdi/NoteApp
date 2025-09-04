package com.razzaghi.noteapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.razzaghi.noteapp.presentation.components.noRippleClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun IconTextChips(label: String, icon: Int, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .noRippleClickable { onClick() }
            .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(painterResource(icon), null)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.background)
    }
}

@Composable
fun TextChips(label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .noRippleClickable { onClick() }
            .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}
