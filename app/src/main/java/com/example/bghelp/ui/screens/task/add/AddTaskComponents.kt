package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.utils.clickableDismissFocus

@Composable
fun <T> Header(
    viewModel: AddTaskViewModel,
    userSectionSelection: T,
    hasToggle: Boolean = true,
    toggleSection: () -> Unit
) where T : Enum<T>, T : SectionMeta {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(Sizes.Icon.M)
                .clickableDismissFocus {
                    viewModel.clearAllInputSelections()
                    toggleSection()
                },
            painter = painterResource(userSectionSelection.iconRes),
            contentDescription = userSectionSelection.headerText
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        Text(
            modifier = Modifier
                .weight(1f)
                .clickableDismissFocus {
                    viewModel.clearAllInputSelections()
                    toggleSection()
                },
            text = userSectionSelection.headerText,
            style = TextStyles.Grey.Bold.M
        )
    }
    
    AddTaskSpacerSmall()
}

@Composable
fun SubContainer(
    modifier: Modifier = Modifier,
    hasInputStart: Boolean = false,
    content: @Composable () -> Unit
) {
    var inputPaddingOffset =
        if (hasInputStart) {
            2 * Sizes.Icon.M - 6.dp
        } else {
            2 * Sizes.Icon.M
        }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = inputPaddingOffset
            )
    ) {
        content()
    }
}