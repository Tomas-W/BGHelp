package com.example.bghelp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ReusableSnackbarHost(
    snackbarMessage: StateFlow<String?>,
    onMessageShown: () -> Unit
) {
    val hostState = remember { SnackbarHostState() }
    val message by snackbarMessage.collectAsState()

    LaunchedEffect(message) {
        message?.let { msg ->
            hostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            onMessageShown() // Clear the message after showing
        }
    }

    SnackbarHost(hostState = hostState) { snackbarData ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { snackbarData.dismiss() },
            contentAlignment = Alignment.Center
        ) {
            Snackbar(snackbarData = snackbarData)
        }
    }
}

