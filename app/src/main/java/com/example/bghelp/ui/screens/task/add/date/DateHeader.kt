package com.example.bghelp.ui.screens.task.add.date

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
import com.example.bghelp.ui.screens.task.add.UserDateSelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun DateHeader(
    viewModel: AddTaskViewModel,
    userDateSelection: UserDateSelection
) {
    val dateStringChoices = remember { mapOf(UserDateSelection.AT_TIME to "At time", UserDateSelection.ALL_DAY to "All day") }
    val dateIconChoices = remember { mapOf(UserDateSelection.AT_TIME to R.drawable.at_time, UserDateSelection.ALL_DAY to R.drawable.all_day) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AddTaskConstants.END_PADDING.dp)
            .clickable {
                viewModel.clearAllInputSelections()
                viewModel.toggleWhenSelection()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clock / Calendar icon
        Icon(
            modifier = Modifier.size(Sizes.Icon.Medium),
            painter = painterResource(dateIconChoices[userDateSelection]!!),
            contentDescription = dateStringChoices[userDateSelection]
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Large))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // At time / All day
            Text(
                text = dateStringChoices[userDateSelection]!!,
                style = TextStyles.Grey.Bold.Medium
            )
             // Dummy toggle
            SelectionToggle(
                selectedIndex = userDateSelection.ordinal,
                numberOfStates = dateStringChoices.size,
                enabled = true,
                onClick = {
                    viewModel.clearAllInputSelections()
                    viewModel.toggleWhenSelection()
                }
            )
        }
    }
}