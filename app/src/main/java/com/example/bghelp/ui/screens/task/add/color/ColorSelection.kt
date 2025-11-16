package com.example.bghelp.ui.screens.task.add.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TaskDefault
import com.example.bghelp.ui.screens.task.add.deselectedStyle

@Composable
fun ColorSelection(
    viewModel: AddTaskViewModel
) {
    val selectedColorId by viewModel.selectedColorId.collectAsState()
    val allColors by viewModel.allColors.collectAsState()

    ColorDropdown(
        colors = allColors,
        selectedColorId = selectedColorId,
        onColorSelected = { colorId ->
            viewModel.setSelectedColorId(colorId)
        }
    )
}

@Composable
fun ColorDropdown(
    colors: List<FeatureColor>,
    selectedColorId: Int?,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val selectedColor = remember(selectedColorId, colors) {
        colors.find { it.id == selectedColorId } ?: colors.firstOrNull()
    }

    val displayText = remember(selectedColor) {
        selectedColor?.name ?: STR.SELECT_COLOR
    }
    val displayColor = remember(selectedColor) {
        selectedColor?.toComposeColor() ?: TaskDefault
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
                    .size(Sizes.Icon.L)
                    .background(displayColor)
            )
            Spacer(modifier = Modifier.width(Sizes.Icon.XS))
            Text(
                text = displayText,
                style = deselectedStyle
            )
        }

        CustomDropdown(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            colors.forEach { color ->
                ColorDropdownItem(
                    label = color.name,
                    color = color.toComposeColor(),
                    onClick = {
                        onColorSelected(color.id)
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
            style = deselectedStyle
        )
    }
}