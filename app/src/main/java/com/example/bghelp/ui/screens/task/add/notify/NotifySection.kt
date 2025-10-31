package com.example.bghelp.ui.screens.task.add.notify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel

@Composable
fun NotifySection(
    viewModel: AddTaskViewModel
) {
    val userNotifySelection by viewModel.userNotifySelection.collectAsState()

    NotifyHeader(
        viewModel = viewModel,
        userNotifySelection = userNotifySelection
    )

    AddTaskSpacerMedium()

    NotifySelection(
        viewModel = viewModel,
        userNotifySelection = userNotifySelection
    )
}

