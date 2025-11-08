package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.screens.task.add.TitleInputType

@Composable
fun TitleSelection(
    viewModel: AddTaskViewModel
) {
    val titleText by viewModel.titleText.collectAsState()
    val activeTitleInput by viewModel.activeTitleInput.collectAsState()

    OutlinedStringInput(
        modifier = Modifier.fillMaxWidth(),
        value = titleText,
        onValueChange = { viewModel.setTitleText(it) },
        hint = AddTaskStrings.TITLE_HINT,
        textStyle = TextStyles.Default.Bold.Small,
        minLines = AddTaskConstants.TITLE_MIN_LINES,
        maxLines = AddTaskConstants.TITLE_MAX_LINES,
        isActive = activeTitleInput == TitleInputType.TITLE,
        onActiveChange = { active ->
            if (active) {
                viewModel.setActiveTitleInput(TitleInputType.TITLE)
            } else {
                viewModel.clearTitleInputSelection()
            }
        }
    )
}