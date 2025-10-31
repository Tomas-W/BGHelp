package com.example.bghelp.ui.screens.task.add.notify

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.SelectionToggle
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserNotifySelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun NotifyHeader(
    viewModel: AddTaskViewModel,
    userNotifySelection: UserNotifySelection
) {
    val notifyStringChoices = remember {
        mapOf(
            UserNotifySelection.OFF to "Don't notify me",
            UserNotifySelection.ON to "Notify me"
        )
    }
    val notifyIconChoices = remember {
        mapOf(
            UserNotifySelection.OFF to R.drawable.notify_off,
            UserNotifySelection.ON to R.drawable.notify_on
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AddTaskConstants.END_PADDING.dp)
            .clickable {
                viewModel.clearAllInputSelections()
                viewModel.toggleNotifySelection()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(Sizes.Icon.Medium),
            painter = painterResource(notifyIconChoices[userNotifySelection]!!),
            contentDescription = notifyStringChoices[userNotifySelection]
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Large))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = notifyStringChoices[userNotifySelection]!!,
                style = TextStyles.Grey.Bold.Medium
            )

            SelectionToggle(
                selectedIndex = userNotifySelection.ordinal,
                numberOfStates = notifyStringChoices.size,
                enabled = true,
                onClick = {
                    viewModel.clearAllInputSelections()
                    viewModel.toggleNotifySelection()
                }
            )
        }
    }
}