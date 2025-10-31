package com.example.bghelp.ui.screens.task.add.notify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableWithUnboundedRipple
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Reminder
import com.example.bghelp.ui.screens.task.add.ReminderType
import com.example.bghelp.ui.screens.task.add.UserNotifySelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun NotifySelection (
    viewModel: AddTaskViewModel,
    userNotifySelection: UserNotifySelection,
) {
    val startReminders by viewModel.startReminders.collectAsState()
    val endReminders by viewModel.endReminders.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (userNotifySelection == UserNotifySelection.ON) {
            AddReminder(
                reminderType = ReminderType.START,
                onClick = { viewModel.addReminder(ReminderType.START) },
                viewModel = viewModel
            )
            AddTaskSpacerSmall()

            startReminders.forEachIndexed { i,  reminder ->
                ReminderItem(
                    reminder = reminder,
                    reminderType = ReminderType.START,
                    viewModel = viewModel
                )
                 if (i != startReminders.size - 1) AddTaskSpacerSmall()
            }
            if (!startReminders.isEmpty()) AddTaskSpacerMedium()

            AddReminder(
                reminderType = ReminderType.END,
                onClick = { viewModel.addReminder(ReminderType.END) },
                viewModel = viewModel
            )
            AddTaskSpacerSmall()

            endReminders.forEachIndexed { i, reminder ->
                ReminderItem(
                    reminder = reminder,
                    reminderType = ReminderType.END,
                    viewModel = viewModel
                )
                if (i != endReminders.size - 1) AddTaskSpacerSmall()
            }
        }
    }
}

@Composable
fun AddReminder(
    reminderType: ReminderType,
    onClick: () -> Unit,
    viewModel: AddTaskViewModel
) {
    val reminderTypeChoices = remember { mapOf(
        ReminderType.START to "Before start",
        ReminderType.END to "Before end") }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Sizes.Icon.Medium + Sizes.Icon.Large))

        Text(
            modifier = Modifier
                .widthIn(min = AddTaskConstants.MIN_WIDTH.dp),
            text = reminderTypeChoices[reminderType]!!,
            style = TextStyles.Default.Medium,
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        WithRipple {
            Box(
                modifier = Modifier
                    .clickableWithUnboundedRipple(
                        onClick = {
                            viewModel.clearReminderInputSelection()
                            onClick()
                        },
                        radius = Sizes.Icon.Small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.Small),
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add reminder ${reminderTypeChoices[reminderType]!!}"
                )
            }
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    reminderType: ReminderType,
    viewModel: AddTaskViewModel
) {
    val activeReminderInput by viewModel.activeReminderInput.collectAsState()
    val isNumberActive = activeReminderInput?.type == reminderType && activeReminderInput?.id == reminder.id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Sizes.Icon.Medium + Sizes.Icon.Large))

        ReminderInput(
            value = reminder.value,
            onValueChange = { newValue ->
                viewModel.updateReminder(reminderType, reminder.id, value = newValue)
            },
            isActive = isNumberActive,
            onActiveChange = { active ->
                if (active) {
                    viewModel.setActiveReminderInput(reminderType, reminder.id)
                } else {
                    viewModel.clearReminderInputSelection()
                }
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.Small))

        TimeUnitDropdown(
            selectedUnit = reminder.timeUnit,
            onUnitSelected = { newUnit ->
                viewModel.updateReminder(reminderType, reminder.id, timeUnit = newUnit)
            },
            onDropdownClick = {
                viewModel.clearReminderInputSelection()
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.Medium))

        Icon(
            modifier = Modifier
                .size(Sizes.Icon.Medium)
                .clickable{ viewModel.removeReminder(reminderType, reminder.id) },
            painter = painterResource(R.drawable.delete),
            contentDescription = "Remove reminder"
        )

//        IconButton(
//            onClick = { viewModel.removeReminder(reminderType, reminder.id) }
//        ) {
//            Icon(
//                modifier = Modifier.size(Sizes.Icon.Medium),
//                painter = painterResource(R.drawable.delete),
//                contentDescription = "Remove reminder"
//            )
//        }
    }
}
