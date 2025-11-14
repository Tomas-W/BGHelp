package com.example.bghelp.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ReusableSnackbarHost(
    snackbarMessage: StateFlow<String?>,
    onMessageShown: () -> Unit
) {
    val hostState = remember { SnackbarHostState() }
    val message by snackbarMessage.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            hostState.showSnackbar(it)
            onMessageShown()
        }
    }

    SnackbarHost(hostState = hostState) { snackbarData ->
        Snackbar(snackbarData)
    }
}

