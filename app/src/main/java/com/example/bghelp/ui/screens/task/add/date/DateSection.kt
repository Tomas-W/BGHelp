package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel

@Composable
fun DateSection(
    viewModel: AddTaskViewModel
) {
    val userDateSelection by viewModel.userDateSelection.collectAsState()

    DateHeader(
        viewModel = viewModel,
        userDateSelection = userDateSelection
    )

    AddTaskSpacerMedium()

    DateTimeSelection(
        viewModel = viewModel,
        userDateSelection = userDateSelection
    )
}

