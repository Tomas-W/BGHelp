package com.example.bghelp.ui.screens.task.add.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserColorChoices
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TaskCyan
import com.example.bghelp.ui.theme.TaskDefault
import com.example.bghelp.ui.theme.TaskGreen
import com.example.bghelp.ui.theme.TaskMagenta
import com.example.bghelp.ui.theme.TaskRed
import com.example.bghelp.ui.theme.TaskYellow
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.screens.task.add.deselectedStyle

@Composable
fun ColorSelection(
    viewModel: AddTaskViewModel
) {
    val selectedColor by viewModel.selectedColor.collectAsState()

    ColorDropdown(
        selectedColor = selectedColor,
        onColorSelected = { color ->
            viewModel.setSelectedColorChoice(color)
        }
    )
}

@Composable
fun ColorDropdown(
    selectedColor: UserColorChoices,
    onColorSelected: (UserColorChoices) -> Unit,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val colorChoices = remember {
        enumValues<UserColorChoices>().toList()
    }
    val colorStringMap = remember {
        mapOf(
            UserColorChoices.DEFAULT to AddTaskStrings.DEFAULT,
            UserColorChoices.RED to AddTaskStrings.RED,
            UserColorChoices.GREEN to AddTaskStrings.GREEN,
            UserColorChoices.YELLOW to AddTaskStrings.YELLOW,
            UserColorChoices.CYAN to AddTaskStrings.CYAN,
            UserColorChoices.MAGENTA to AddTaskStrings.MAGENTA
        )
    }
    val colorMap = remember {
        mapOf(
            UserColorChoices.DEFAULT to TaskDefault,
            UserColorChoices.RED to TaskRed,
            UserColorChoices.GREEN to TaskGreen,
            UserColorChoices.YELLOW to TaskYellow,
            UserColorChoices.CYAN to TaskCyan,
            UserColorChoices.MAGENTA to TaskMagenta
        )
    }

    val displayText = remember(selectedColor, colorStringMap) {
        colorStringMap[selectedColor] ?: AddTaskStrings.SELECT_COLOR
    }
    val displayColor = remember(selectedColor, colorMap) {
        colorMap[selectedColor] ?: TaskDefault
    }

    Box(
        modifier = modifier
        .wrapContentWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onDropdownClick()
                    isExpanded = true
                },
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Sizes.Icon.Large)
                    .background(displayColor)
            )
            Spacer(modifier = Modifier.width(Sizes.Icon.ExtraSmall))
            Text(
                text = displayText,
                style = deselectedStyle
            )
        }

        CustomDropdown(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            colorChoices.forEach { color ->
                ColorDropdownItem(
                    label = colorStringMap[color] ?: color.name,
                    color = colorMap[color] ?: TaskDefault,
                    onClick = {
                        onColorSelected(color)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ColorDropdownItem(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyles.Default.Small
        )
    }
}