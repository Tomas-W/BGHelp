package com.example.bghelp.ui.screens.task.add

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
import com.example.bghelp.ui.components.DummyTwoToggle
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun AtTimeAllDay(
    viewModel: AddTaskViewModel,
    whenSelection: WhenSelection
) {
    val whenStringChoices = remember { mapOf(WhenSelection.AT_TIME to "At time", WhenSelection.ALL_DAY to "All day") }
    val whenIconChoices = remember { mapOf(WhenSelection.AT_TIME to R.drawable.at_time, WhenSelection.ALL_DAY to R.drawable.all_day) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp)
            .clickable {
                viewModel.toggleWhenSelection()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clock / Calendar icon
        Icon(
            modifier = Modifier.size(Sizes.Icon.Medium),
            painter = painterResource(whenIconChoices[whenSelection]!!),
            contentDescription = whenStringChoices[whenSelection]
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
                text = whenStringChoices[whenSelection]!!,
                style = TextStyles.Default.Medium
            )
             // Dummy toggle
            DummyTwoToggle(
                selectedIndex = whenSelection.ordinal,
                enabled = true,
                onOverlayClick = {
                    viewModel.toggleWhenSelection()
                }
            )
        }
    }
}