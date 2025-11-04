package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.bghelp.R
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRippleDismiss
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Reminder
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.RemindType
import com.example.bghelp.ui.screens.task.add.UserRemindSelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun RemindSelection (
    viewModel: AddTaskViewModel,
) {
    val startReminders by viewModel.startReminders.collectAsState()
    val endReminders by viewModel.endReminders.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        AddTaskSpacerMedium()
        // Before Start
        AddReminder(
            remindType = RemindType.START,
            onClick = { viewModel.addReminder(RemindType.START) },
            viewModel = viewModel
        )
        AddTaskSpacerSmall()

        // Before start items
        startReminders.forEachIndexed { i, reminder ->
            ReminderItem(
                reminder = reminder,
                remindType = RemindType.START,
                viewModel = viewModel
            )
            if (i != startReminders.size - 1) AddTaskSpacerSmall()
        }
        if (startReminders.isNotEmpty()) AddTaskSpacerMedium()

        // Before end
        AddReminder(
            remindType = RemindType.END,
            onClick = { viewModel.addReminder(RemindType.END) },
            viewModel = viewModel
        )
        AddTaskSpacerSmall()

        // Before end items
        endReminders.forEachIndexed { i, reminder ->
            ReminderItem(
                reminder = reminder,
                remindType = RemindType.END,
                viewModel = viewModel
            )
            if (i != endReminders.size - 1) AddTaskSpacerSmall()
        }
    }
}

@Composable
fun AddReminder(
    remindType: RemindType,
    onClick: () -> Unit,
    viewModel: AddTaskViewModel
) {
    val remindTypeChoices = remember { mapOf(
        RemindType.START to AddTaskStrings.BEFORE_START,
        RemindType.END to AddTaskStrings.BEFORE_END) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(2 * Sizes.Icon.Large))

        // Before / after start
        Text(
            modifier = Modifier
                .widthIn(min = AddTaskConstants.MIN_WIDTH.dp),
            text = remindTypeChoices[remindType]!!,
            style = TextStyles.Default.Medium,
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Medium))

        // + Icon
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRippleDismiss(
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
                    contentDescription = AddTaskStrings.ADD_REMINDER
                )
            }
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    remindType: RemindType,
    viewModel: AddTaskViewModel
) {
    val activeReminderInput by viewModel.activeReminderInput.collectAsState()
    val isNumberActive = activeReminderInput?.type == remindType && activeReminderInput?.id == reminder.id

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(Sizes.Icon.Medium + Sizes.Icon.Large))

        // Input field
        ReminderInput(
            value = reminder.value,
            onValueChange = { newValue ->
                viewModel.updateReminder(remindType, reminder.id, value = newValue)
            },
            isActive = isNumberActive,
            onActiveChange = { active ->
                if (active) {
                    viewModel.setActiveReminderInput(remindType, reminder.id)
                } else {
                    viewModel.clearReminderInputSelection()
                }
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.Small))

        // Dropdown
        TimeUnitDropdown(
            selectedUnit = reminder.timeUnit,
            onUnitSelected = { newUnit ->
                viewModel.updateReminder(remindType, reminder.id, timeUnit = newUnit)
            },
            onDropdownClick = {
                viewModel.clearReminderInputSelection()
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Size.Medium))

        // Delete icon
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRippleDismiss(
                        onClick = { viewModel.removeReminder(remindType, reminder.id) },
                        radius = Sizes.Icon.Medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.Medium),
                    painter = painterResource(R.drawable.delete),
                    contentDescription = AddTaskStrings.REMOVE_REMINDER
                )
            }
        }
    }
}
