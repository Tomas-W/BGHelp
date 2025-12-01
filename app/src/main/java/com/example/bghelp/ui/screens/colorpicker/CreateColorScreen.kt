package com.example.bghelp.ui.screens.colorpicker

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.domain.model.ExampleTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.Task
import com.example.bghelp.ui.components.ButtonRow
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.components.PreviewScreen
import com.example.bghelp.ui.components.ReusableSnackbarHost
import com.example.bghelp.ui.screens.task.main.DayComponent
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.utils.dismissKeyboardOnTap
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val defaultColorNames = listOf(
    "Default",
    "Red",
    "Green",
    "Blue",
    "Yellow",
    "Cyan",
    "Magenta"
)

@Composable
fun CreateColorScreen(
    navController: NavController,
    viewModel: CreateColorViewModel = hiltViewModel()
) {
    val selectedColor by viewModel.selectedColor.collectAsState()
    val saving by viewModel.saving.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val createdColorId by viewModel.createdColorId.collectAsState()
    val isExampleExpanded by viewModel.isExampleExpanded.collectAsState()
    val colorName by viewModel.colorName.collectAsState()
    val isColorNameActive by viewModel.isColorNameActive.collectAsState()
    val isColorNameValid by viewModel.isColorNameValid.collectAsState()

    // Handle color creation result
    LaunchedEffect(saved, createdColorId) {
        if (saved && createdColorId != null) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(ColorNavigationKeys.RESULT, createdColorId)
            navController.popBackStack()
        }
    }

    val exampleTask = remember(selectedColor) {
        val baseTask = ExampleTask().toTask()
        val featureColor = selectedColor.toFeatureColor()
        baseTask.copy(color = featureColor)
    }

    CreateColorContent(
        isExampleExpanded = isExampleExpanded,
        colorName = colorName,
        isColorNameActive = isColorNameActive,
        isColorNameValid = isColorNameValid,
        saving = saving,
        exampleTask = exampleTask,
        onColorChanged = { envelope ->
            viewModel.onColorChanged(envelope)
            viewModel.clearColorNameInputSelection()
        },
        onColorNameChange = viewModel::setColorName,
        onColorNameActiveChange = viewModel::setColorNameActive,
        onToggleExpanded = {
            viewModel.clearColorNameInputSelection()
            viewModel.toggleExpandExample()
        },
        onSaveClick = { viewModel.saveColor() },
        onCancelClick = { navController.popBackStack() },
        snackbarMessage = viewModel.snackbarMessage,
        onSnackbarMessageShown = { viewModel.clearSnackbarMessage() }
    )
}

@Composable
private fun CreateColorContent(
    isExampleExpanded: Boolean,
    colorName: String,
    isColorNameActive: Boolean,
    isColorNameValid: Boolean,
    saving: Boolean,
    exampleTask: Task,
    onColorChanged: (ColorEnvelope) -> Unit,
    onColorNameChange: (String) -> Unit,
    onColorNameActiveChange: (Boolean) -> Unit,
    onToggleExpanded: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    snackbarMessage: StateFlow<String?>,
    onSnackbarMessageShown: () -> Unit
) {
    val controller = rememberColorPickerController()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .dismissKeyboardOnTap {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MainContentContainer(
                modifier = Modifier
                    .weight(1f, fill = true),
                spacing = 24
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    controller = controller,
                    onColorChanged = { envelope: ColorEnvelope ->
                        onColorChanged(envelope)
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                OutlinedStringInput(
                    value = colorName,
                    onValueChange = onColorNameChange,
                    hint = stringResource(R.string.extra_color_name),
                    textStyle = MaterialTheme.typography.lTextDefault,
                    isMultiLine = false,
                    minLines = 1,
                    maxLines = 1,
                    isActive = isColorNameActive,
                    onActiveChange = onColorNameActiveChange,
                    onDone = {}
                )

                DayComponent(
                    task = exampleTask,
                    isExpanded = isExampleExpanded,
                    onToggleExpanded = { _ ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onToggleExpanded()
                    }
                )
            }

            ButtonRow(
                isValid = isColorNameValid,
                isLoading = saving,
                firstLabel = if (saving) stringResource(R.string.button_saving) else stringResource(R.string.button_save_color),
                firstOnClick = onSaveClick,
                secondLabel = stringResource(R.string.button_cancel),
                secondOnClick = onCancelClick,
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
                snackbarMessage = snackbarMessage,
                onMessageShown = { onSnackbarMessageShown() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateColorScreenPreview() {
    val selectedColor = Color(0xFF6200EE)
    val exampleTask = remember(selectedColor) {
        val baseTask = ExampleTask().toTask()
        val featureColor = selectedColor.toFeatureColor()
        baseTask.copy(color = featureColor)
    }

    val previewSnackbarMessage = remember { MutableStateFlow<String?>(null) }

    PreviewScreen {
        CreateColorContent(
            isExampleExpanded = true,
            colorName = "Example color",
            isColorNameActive = false,
            isColorNameValid = true,
            saving = false,
            exampleTask = exampleTask,
            onColorChanged = {},
            onColorNameChange = {},
            onColorNameActiveChange = {},
            onToggleExpanded = {},
            onSaveClick = {},
            onCancelClick = {},
            snackbarMessage = previewSnackbarMessage,
            onSnackbarMessageShown = {}
        )
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
