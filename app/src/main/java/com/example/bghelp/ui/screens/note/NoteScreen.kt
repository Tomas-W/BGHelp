package com.example.bghelp.ui.screens.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.MainContentContainer

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        MainContentContainer(
            spacing = 10
        ) {
            Text(
                text = "displayLarge",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = "displayMedium",
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = "displaySmall",
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = "headlineLarge",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "headlineMedium",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "headlineSmall",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "titleLarge",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "titleMedium",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "titleSmall",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "bodyLarge",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "bodyMedium",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "bodySmall",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "labelLarge",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "labelMedium",
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = "labelSmall",
                style = MaterialTheme.typography.labelSmall
            )

        }
    }
}

