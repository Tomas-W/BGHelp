package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.screens.task.add.TitleInputType

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
        hint = STR.INFO_HINT,
        textStyle = TextStyles.Default.Italic.S,
        minLines = CONST.INFO_MIN_LINES,
        maxLines = CONST.INFO_MAX_LINES,
        isActive = activeTitleInput == TitleInputType.INFO,
        onActiveChange = { active ->
            if (active) {
                viewModel.setActiveTitleInput(TitleInputType.INFO)
            } else {
                viewModel.clearTitleInputSelection()
            }
        }
    )
}
