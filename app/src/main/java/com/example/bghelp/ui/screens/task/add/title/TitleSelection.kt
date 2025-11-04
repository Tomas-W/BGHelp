package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.UserTitleSelection
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.components.OutlinedStringInput

@Composable
fun TitleSelection(
    viewModel: AddTaskViewModel,
    userTitleSelection: UserTitleSelection
) {
    val titleText by viewModel.titleText.collectAsState()
    val infoText by viewModel.infoText.collectAsState()
    val activeTitleInput by viewModel.activeTitleInput.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Title input
        OutlinedStringInput(
            modifier = Modifier,
            value = titleText,
            onValueChange = { viewModel.setTitleText(it) },
            hint = AddTaskStrings.TITLE_HINT,
            textStyle = TextStyles.Default.Bold.Small,
            minLines = AddTaskConstants.TITLE_MIN_LINES,
            maxLines = AddTaskConstants.TITLE_MAX_LINES,
            isActive = activeTitleInput == AddTaskViewModel.TitleInputType.TITLE,
            onActiveChange = { active ->
                if (active) {
                    viewModel.setActiveTitleInput(AddTaskViewModel.TitleInputType.TITLE)
                } else {
                    viewModel.clearTitleInputSelection()
                }
            }
        )

        if (userTitleSelection == UserTitleSelection.TITLE_AND_INFO) {
            AddTaskSpacerSmall()

            // Info input (twice as high)
            OutlinedStringInput(
                modifier = Modifier,
                value = infoText,
                onValueChange = { viewModel.setInfoText(it) },
                hint = AddTaskStrings.INFO_HINT,
                textStyle = TextStyles.Default.Italic.Small,
                minLines = AddTaskConstants.INFO_MIN_LINES,
                maxLines = AddTaskConstants.INFO_MAX_LINES,
                isActive = activeTitleInput == AddTaskViewModel.TitleInputType.INFO,
                onActiveChange = { active ->
                    if (active) {
                        viewModel.setActiveTitleInput(AddTaskViewModel.TitleInputType.INFO)
                    } else {
                        viewModel.clearTitleInputSelection()
                    }
                }
            )
        }
    }
}