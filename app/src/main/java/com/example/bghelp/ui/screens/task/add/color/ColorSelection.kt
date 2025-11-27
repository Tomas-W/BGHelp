package com.example.bghelp.ui.screens.task.add.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.deselectedDropdownStyle
import com.example.bghelp.ui.components.selectedDropdownStyle
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.colorpicker.defaultColorNames
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextDefault

@Composable
fun ColorSelection(
    viewModel: AddTaskViewModel,
    navController: NavController
) {
    val selectedColorId by viewModel.selectedColorId.collectAsState()
    val allColors by viewModel.allColors.collectAsState()

    ColorDropdown(
        colors = allColors,
        selectedColorId = selectedColorId,
        onColorSelected = { colorId ->
            viewModel.setSelectedColorId(colorId)
        },
        navController = navController
    )
}

@Composable
fun ColorDropdown(
    colors: List<FeatureColor>,
    selectedColorId: Int?,
    onColorSelected: (Int?) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val selectedColor = remember(selectedColorId, colors) {
        selectedColorId?.let { id -> colors.find { it.id == id } }
    }

    val selectColorText = stringResource(R.string.task_select_color)
    val addColorText = stringResource(R.string.task_add_color)
    
    val defaultText = selectColorText
    val displayText = selectedColor?.let { getLocalizedColorName(it.name) } ?: defaultText
    val defaultColor = Color.Transparent
    val displayColor = remember(selectedColor) {
        selectedColor?.toComposeColor() ?: defaultColor
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Sizes.Icon.L)
                    .background(displayColor)
            )
            Spacer(modifier = Modifier.width(Sizes.Icon.XS))
            Text(
                text = displayText,
                style = MaterialTheme.typography.lTextDefault
            )
        }

        CustomDropdown(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownItem(
                label = selectColorText,
                onClick = {
                    onColorSelected(null)
                    isExpanded = false
                },
                textStyle = if (selectedColorId == null) {
                    selectedDropdownStyle()
                } else {
                    deselectedDropdownStyle()
                },
                backgroundColor = null,
                spacing = Sizes.Icon.M
            )
            DropdownItem(
                label = addColorText,
                onClick = {
                    navController.navigate(Screen.Options.ColorPicker.route) {
                        launchSingleTop = true
                    }
                    isExpanded = false
                },
                textStyle = deselectedDropdownStyle(),
                backgroundColor = null,
                spacing = Sizes.Icon.M
            )
            colors.forEach { color ->
                val localizedName = getLocalizedColorName(color.name)
                DropdownItem(
                    label = localizedName,
                    onClick = {
                        onColorSelected(color.id)
                        isExpanded = false
                    },
                    textStyle = if (color.id == selectedColorId) {
                        selectedDropdownStyle()
                    } else {
                        deselectedDropdownStyle()
                    },
                    backgroundColor = color.toComposeColor(),
                    spacing = Sizes.Icon.M
                )
            }
        }
    }
}

@Composable
private fun getLocalizedColorName(name: String): String {
    return if (name in defaultColorNames) {
        when (name) {
            "Default" -> stringResource(R.string.extra_color_default)
            "Red" -> stringResource(R.string.extra_color_red)
            "Green" -> stringResource(R.string.extra_color_green)
            "Blue" -> stringResource(R.string.extra_color_blue)
            "Yellow" -> stringResource(R.string.extra_color_yellow)
            "Cyan" -> stringResource(R.string.extra_color_cyan)
            "Magenta" -> stringResource(R.string.extra_color_magenta)
            else -> name
        }
    } else {
        name
    }
}