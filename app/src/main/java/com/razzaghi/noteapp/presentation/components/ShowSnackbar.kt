package com.razzaghi.noteapp.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ShowSnackbar(
    modifier: Modifier,
    title: String,
    onDismiss: () -> Unit
) {


    Snackbar(
        modifier = modifier.padding(16.dp),
        action = {
            Button(onClick = {
                // Dismiss the Snackbar
                onDismiss()
            }) {
                Text(text = "OK")
            }
        }
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
    }
}