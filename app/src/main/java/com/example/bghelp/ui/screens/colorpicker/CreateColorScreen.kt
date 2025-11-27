package com.example.bghelp.ui.screens.colorpicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.domain.model.ExampleTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.ui.components.ButtonRow
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.components.ReusableSnackbarHost
import com.example.bghelp.ui.screens.task.main.DayComponent
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.utils.dismissKeyboardOnTap
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun CreateColorScreen(
    navController: NavController,
    viewModel: CreateColorViewModel = hiltViewModel()
) {
    val selectedColor by viewModel.selectedColor.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val isExampleExpanded by viewModel.isExampleExpanded.collectAsState()
    val colorName by viewModel.colorName.collectAsState()
    val isColorNameActive by viewModel.isColorNameActive.collectAsState()
    val isColorNameValid by viewModel.isColorNameValid.collectAsState()

    LaunchedEffect(saved) {
        if (saved) {
            navController.popBackStack()
        }
    }

    val controller = rememberColorPickerController()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val exampleTask = remember(selectedColor) {
        val baseTask = ExampleTask().toTask()
        val featureColor = selectedColor.toFeatureColor()
        baseTask.copy(color = featureColor)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap {
                viewModel.clearColorNameInputSelection()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MainContentContainer(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .padding(top = 32.dp),
                spacing = 24
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    controller = controller,
                    onColorChanged = { envelope: ColorEnvelope ->
                        viewModel.onColorChanged(envelope)
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.clearColorNameInputSelection()
                    }
                )

                OutlinedStringInput(
                    value = colorName,
                    onValueChange = viewModel::setColorName,
                    hint = "Name",
                    textStyle = MaterialTheme.typography.lTextDefault,
                    isMultiLine = false,
                    minLines = 1,
                    maxLines = 1,
                    isActive = isColorNameActive,
                    onActiveChange = viewModel::setColorNameActive,
                    onDone = {}
                )

                DayComponent(
                    task = exampleTask,
                    isExpanded = isExampleExpanded,
                    onToggleExpanded = { _ ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        viewModel.clearColorNameInputSelection()
                        viewModel.toggleExpandExample()
                    }
                )
            }

            ButtonRow(
                isValid = isColorNameValid,
                isLoading = saving,
                firstLabel = if (saving) "Saving..." else "Save Color",
                firstOnClick = { viewModel.saveColor() },
                secondLabel = "Cancel",
                secondOnClick = { navController.popBackStack() },
            )
        }

        // Snackbar positioned at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ReusableSnackbarHost(
                snackbarMessage = viewModel.snackbarMessage,
                onMessageShown = { viewModel.clearSnackbarMessage() }
            )
        }
    }
}

private fun Color.toFeatureColor(): FeatureColor {
    val red = (red * 255f).toInt().coerceIn(0, 255)
    val green = (green * 255f).toInt().coerceIn(0, 255)
    val blue = (blue * 255f).toInt().coerceIn(0, 255)
    // Preview always shows with 0.4 alpha
    val alpha = 0.25f
    val hex = "#%02X%02X%02X".format(red, green, blue)
    return FeatureColor(
        id = 0,
        name = "Preview $hex",
        red = red,
        green = green,
        blue = blue,
        alpha = alpha
    )
}
