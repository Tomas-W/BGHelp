package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.components.OutlinedStringInput

@Composable
fun InfoSelection(
    viewModel: AddTaskViewModel
) {
    val infoText by viewModel.infoText.collectAsState()
    val activeTitleInput by viewModel.activeTitleInput.collectAsState()

    AddTaskSpacerSmall()

    OutlinedStringInput(
        modifier = Modifier.fillMaxWidth(),
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
