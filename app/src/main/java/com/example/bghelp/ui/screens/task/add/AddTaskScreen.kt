package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.BGHelpTheme

object AddTaskConstants {
    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100
}

enum class WhenSelection { AT_TIME, ALL_DAY }
enum class DateField { START, END }
enum class TimeField { START, END }
enum class TimeSegment { HOUR, MINUTE }

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val whenSelection by viewModel.whenSelection.collectAsState()

    MainContentContainer {

        AtTimeAllDay(
            viewModel = viewModel,
            whenSelection = whenSelection
        )

        AddTaskSpacer()

        DateTimeSelection(
            viewModel = viewModel,
            whenSelection = whenSelection
        )

        AddTaskDivider()
    }
}

@Composable
fun AddTaskSpacer() {
    Spacer(modifier = Modifier.height(Sizes.Size.Small))
}

@Composable
fun AddTaskSpacerHalf() {
    Spacer(modifier = Modifier.height(Sizes.Size.ExtraSmall))
}

@Composable
fun AddTaskDivider() {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(12.dp))
}

@Preview(showBackground = true)
@Composable
private fun AddTaskScreenPreview() {
    BGHelpTheme {
        Box(modifier = Modifier.systemBarsPadding()) {
            AddTaskScreen()
        }
    }
}
