package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerLarge
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.UserTitleSelection

@Composable
fun TitleSection(
    viewModel: AddTaskViewModel
) {
    val userTitleSelection by viewModel.userTitleSelection.collectAsState()
    val titleStringChoices = remember { mapOf(
        UserTitleSelection.TITLE_ONLY to AddTaskStrings.TITLE_ONLY,
        UserTitleSelection.TITLE_AND_INFO to AddTaskStrings.TITLE_AND_INFO) }
    val titleIconChoices = remember { mapOf(
        UserTitleSelection.TITLE_ONLY to R.drawable.title_only,
        UserTitleSelection.TITLE_AND_INFO to R.drawable.title_and_info) }

    AddTaskHeader(
        viewModel = viewModel,
        userSectionSelection = userTitleSelection,
        stringChoices = titleStringChoices,
        iconChoices = titleIconChoices,
        toggleSection = { viewModel.toggleTitleSelection() }
    )

    AddTaskSpacerMedium()

    TitleSelection(
        viewModel = viewModel,
        userTitleSelection = userTitleSelection
    )
}
