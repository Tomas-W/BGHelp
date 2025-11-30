package com.example.bghelp.ui.screens.task.add.title

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.bghelp.R
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.TitleInputType
import com.example.bghelp.ui.theme.lTextItalic
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST

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
        hint = stringResource(R.string.task_info_hint),
        textStyle = MaterialTheme.typography.lTextItalic,
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
