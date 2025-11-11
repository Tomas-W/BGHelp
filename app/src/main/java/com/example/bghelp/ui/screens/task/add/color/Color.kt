package com.example.bghelp.ui.screens.task.add.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserColorSelection

@Composable
fun Color(
    viewModel: AddTaskViewModel
) {
    val userColorSelection by viewModel.userColorSelection.collectAsState()

    Header(
        viewModel = viewModel,
        userSectionSelection = userColorSelection,
        toggleSection = { viewModel.toggleColorSelection() }
    )

    if (userColorSelection == UserColorSelection.ON) {
        SubContainer {
            ColorSelection(
                viewModel = viewModel
            )
        }
    }
}