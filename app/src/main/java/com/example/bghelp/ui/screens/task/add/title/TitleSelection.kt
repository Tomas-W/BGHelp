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
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.TitleInputType
import com.example.bghelp.ui.theme.lTextSemi
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST

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
        hint = stringResource(R.string.task_info_hint),
        textStyle = MaterialTheme.typography.lTextSemi,
        minLines = CONST.TITLE_MIN_LINES,
        maxLines = CONST.TITLE_MAX_LINES,
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