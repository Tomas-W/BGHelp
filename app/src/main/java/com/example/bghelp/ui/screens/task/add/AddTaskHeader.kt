package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.SelectionToggle
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.utils.clickableDismissFocus

@Composable
fun <T : Enum<T>> AddTaskHeader(
    viewModel: AddTaskViewModel,
    userSectionSelection: T,
    stringChoices: Map<T, String>,
    iconChoices: Map<T, Int>,
    toggleSection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AddTaskConstants.END_PADDING.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(Sizes.Icon.Large)
                .clickableDismissFocus {
                    viewModel.clearAllInputSelections()
                    toggleSection()
                },
            painter = painterResource(iconChoices[userSectionSelection]!!),
            contentDescription = stringChoices[userSectionSelection]
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.Large))

        Text(
            modifier = Modifier
                .weight(1f)
                .clickableDismissFocus {
                    viewModel.clearAllInputSelections()
                    toggleSection()
                },
            text = stringChoices[userSectionSelection]!!,
            style = TextStyles.Grey.Bold.Medium
        )

        SelectionToggle(
            selectedIndex = userSectionSelection.ordinal,
            numberOfStates = stringChoices.size,
            enabled = true,
            onClick = {
                viewModel.clearAllInputSelections()
                toggleSection()
            }
        )
    }
}
