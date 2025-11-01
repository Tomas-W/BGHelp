package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.UserDateSelection

@Composable
fun DateSection(
    viewModel: AddTaskViewModel
) {
    val userDateSelection by viewModel.userDateSelection.collectAsState()
    val dateStringChoices = remember { mapOf(
        UserDateSelection.AT_TIME to AddTaskStrings.AT_TIME,
        UserDateSelection.ALL_DAY to AddTaskStrings.ALL_DAY) }
    val dateIconChoices = remember { mapOf(
        UserDateSelection.AT_TIME to R.drawable.at_time,
        UserDateSelection.ALL_DAY to R.drawable.all_day) }

    AddTaskHeader(
        viewModel = viewModel,
        userSectionSelection = userDateSelection,
        stringChoices = dateStringChoices,
        iconChoices = dateIconChoices,
        toggleSection = { viewModel.toggleDateSelection() }
    )

    AddTaskSpacerMedium()

    DateTimeSelection(
        viewModel = viewModel,
        userDateSelection = userDateSelection
    )
}
