package com.example.bghelp.ui.screens.task.add.repeat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserRepeatSelection

@Composable
fun Repeat(
    viewModel: AddTaskViewModel
) {
    val userRepeatSelection by viewModel.userRepeatSelection.collectAsState()

    Header(
        viewModel = viewModel,
        userSectionSelection = userRepeatSelection,
        toggleSection = { viewModel.toggleRepeatSelection() }
    )

    when (userRepeatSelection) {
        UserRepeatSelection.WEEKLY -> {
            SubContainer {
                WeeklySelection(
                    viewModel = viewModel
                )
            }
        }
        UserRepeatSelection.MONTHLY -> {
                MonthlySelection(
                    viewModel = viewModel
                )
        }
        else -> {}
    }
}