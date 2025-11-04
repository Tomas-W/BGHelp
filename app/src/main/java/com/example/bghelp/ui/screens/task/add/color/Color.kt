package com.example.bghelp.ui.screens.task.add.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserColorSelection

@Composable
fun ColorSection(
    viewModel: AddTaskViewModel
) {
    val userColorSelection by viewModel.userColorSelection.collectAsState()
    val colorStringChoices = remember { mapOf(
        UserColorSelection.DEFAULT to AddTaskStrings.DEFAULT_COLOR,
        UserColorSelection.CUSTOM to AddTaskStrings.CUSTOM_COLOR
        )
    }
    val colorIconChoices = remember { mapOf(
        UserColorSelection.DEFAULT to R.drawable.default_color,
        UserColorSelection.CUSTOM to R.drawable.custom_color
        )
    }

    AddTaskHeader(
        viewModel = viewModel,
        userSectionSelection = userColorSelection,
        stringChoices = colorStringChoices,
        iconChoices = colorIconChoices,
        toggleSection = { viewModel.toggleColorSelection() }
    )

    AddTaskSpacerMedium()

    if (userColorSelection == UserColorSelection.CUSTOM) {
        ColorSelection(
            viewModel = viewModel
        )
    }
}