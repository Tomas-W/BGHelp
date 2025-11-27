package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRippleDismiss
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.TimeField
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST

@Composable
fun TimeSelection(
    viewModel: AddTaskViewModel
) {
    val timeStartSelection by viewModel.timeStartSelection.collectAsState()
    val timeEndSelection by viewModel.timeEndSelection.collectAsState()
    val activeTimeField by viewModel.activeTimeField.collectAsState()
    val activeTimeSegment by viewModel.activeTimeSegment.collectAsState()
    val isEndTimeVisible by viewModel.isEndTimeVisible.collectAsState()
    val keyboardDismissKey by viewModel.keyboardDismissKey.collectAsState()

    AddTaskSpacerSmall()

    val inputPaddingOffset = 6.dp

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // startTime
        TimeSelectionInput(
            modifier = Modifier.widthIn(min = CONST.MIN_WIDTH.dp + inputPaddingOffset),
            time = timeStartSelection,
            timeField = TimeField.START,
            activeTimeField = activeTimeField,
            activeTimeSegment = activeTimeSegment,
            onTimeChanged = { viewModel.setTimeStart(it) },
            onTimeInputClicked = {
                viewModel.onTimeInputClicked()
            },
            onSegmentSelected = { field, segment ->
                viewModel.setActiveTimeInput(field, segment)
            },
            onSelectionCleared = {
                viewModel.clearTimeInputSelection()
            },
            keyboardDismissKey = keyboardDismissKey
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        // Double arrow
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRippleDismiss(
                        onClick = {
                            viewModel.toggleEndTimeVisible()
                        },
                        radius = Sizes.Icon.S
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.S),
                    painter = painterResource(R.drawable.double_arrow_right),
                    contentDescription = if (isEndTimeVisible) stringResource(R.string.task_hide_end_time)
                                         else stringResource(R.string.task_show_end_time)
                )
            }
        }

        Spacer(modifier = Modifier.width(Sizes.Icon.M - inputPaddingOffset))

        // endTime
        TimeSelectionInput(
            modifier = Modifier
                .widthIn(min = CONST.MIN_WIDTH.dp)
                .alpha(if (isEndTimeVisible) 1f else 0f),
            time = timeEndSelection ?: timeStartSelection.plusHours(1),
            timeField = TimeField.END,
            activeTimeField = activeTimeField,
            activeTimeSegment = activeTimeSegment,
            onTimeChanged = { if (isEndTimeVisible) viewModel.setTimeEnd(it) },
            onTimeInputClicked = {
                if (isEndTimeVisible) {
                    viewModel.onTimeInputClicked()
                }
            },
            onSegmentSelected = { field, segment ->
                if (isEndTimeVisible) {
                    viewModel.setActiveTimeInput(field, segment)
                }
            },
            onSelectionCleared = {
                if (isEndTimeVisible) {
                    viewModel.clearTimeInputSelection()
                }
            },
            keyboardDismissKey = keyboardDismissKey
        )
    }
}
