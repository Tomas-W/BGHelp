package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.UserTitleSelection

@Composable
fun Title(
    viewModel: AddTaskViewModel
) {
    val userTitleSelection by viewModel.userTitleSelection.collectAsState()

    Header(
        viewModel = viewModel,
        userSectionSelection = userTitleSelection,
        toggleSection = { viewModel.toggleTitleSelection() }
    )

    TitleSelection(
        viewModel = viewModel
    )

    if (userTitleSelection == UserTitleSelection.ON) {
        InfoSelection(
            viewModel = viewModel
        )
    }

    AddTaskSpacerSmall()
}
