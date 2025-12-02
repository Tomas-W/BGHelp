package com.example.bghelp.ui.screens.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.domain.model.ExampleTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.ui.components.ButtonRow
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.OutlinedStringInput
import com.example.bghelp.ui.components.ReusableSnackbarHost
import com.example.bghelp.ui.screens.task.main.DayComponent
import com.example.bghelp.ui.theme.BackgroundGrey
import com.example.bghelp.ui.theme.DarkBackgroundGrey
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.utils.dismissKeyboardOnTap
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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

    val lightColor by viewModel.lightColor.collectAsState()
    val lightBrightness by viewModel.lightBrightness.collectAsState()
    val lightAlpha by viewModel.lightAlpha.collectAsState()
    val lightTextColor by viewModel.lightTextColor.collectAsState()
    val lightTextSliderPosition by viewModel.lightTextSliderPosition.collectAsState()

    val darkColor by viewModel.darkColor.collectAsState()
    val darkBrightness by viewModel.darkBrightness.collectAsState()
    val darkAlpha by viewModel.darkAlpha.collectAsState()
    val darkTextColor by viewModel.darkTextColor.collectAsState()
    val darkTextSliderPosition by viewModel.darkTextSliderPosition.collectAsState()

    val saving by viewModel.saving.collectAsState()
    val saved by viewModel.saved.collectAsState()

    val createdColorId by viewModel.createdColorId.collectAsState()
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

    CreateColorContent(
        selectedColor = selectedColor,
        onColorChanged = { envelope ->
            viewModel.onColorChanged(envelope)
            viewModel.clearColorNameInputSelection()
        },

        lightColor = lightColor,
        lightBrightness = lightBrightness,
        onLightBrightnessChange = viewModel::setLightBrightness,
        lightAlpha = lightAlpha,
        onLightAlphaChange = viewModel::setLightAlpha,
        lightTextColor = lightTextColor,
        lightTextSliderPosition = lightTextSliderPosition,
        onLightTextSliderChange = viewModel::setLightTextSliderPosition,

        darkColor = darkColor,
        darkBrightness = darkBrightness,
        onDarkBrightnessChange = viewModel::setDarkBrightness,
        darkAlpha = darkAlpha,
        onDarkAlphaChange = viewModel::setDarkAlpha,
        darkTextColor = darkTextColor,
        darkTextSliderPosition = darkTextSliderPosition,
        onDarkTextSliderChange = viewModel::setDarkTextSliderPosition,

        colorName = colorName,
        onColorNameChange = viewModel::setColorName,
        isColorNameActive = isColorNameActive,
        onColorNameActiveChange = viewModel::setColorNameActive,
        isColorNameValid = isColorNameValid,

        saving = saving,
        onSaveClick = { viewModel.saveColor() },
        onCancelClick = { navController.popBackStack() },
        onToggleExpanded = {
            viewModel.clearColorNameInputSelection()
        },
        snackbarMessage = viewModel.snackbarMessage,
        onSnackbarMessageShown = { viewModel.clearSnackbarMessage() }
    )
}

@Composable
private fun CreateColorContent(
    selectedColor: Color,
    onColorChanged: (ColorEnvelope) -> Unit,

    lightColor: Color,
    lightBrightness: Float,
    onLightBrightnessChange: (Float) -> Unit,
    lightAlpha: Float,
    onLightAlphaChange: (Float) -> Unit,
    lightTextColor: Color,
    lightTextSliderPosition: Float,
    onLightTextSliderChange: (Float) -> Unit,

    darkColor: Color,
    darkBrightness: Float,
    onDarkBrightnessChange: (Float) -> Unit,
    darkAlpha: Float,
    onDarkAlphaChange: (Float) -> Unit,
    darkTextColor: Color,
    darkTextSliderPosition: Float,
    onDarkTextSliderChange: (Float) -> Unit,

    colorName: String,
    onColorNameChange: (String) -> Unit,
    isColorNameActive: Boolean,
    onColorNameActiveChange: (Boolean) -> Unit,
    isColorNameValid: Boolean,
    
    saving: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onToggleExpanded: () -> Unit,
    snackbarMessage: StateFlow<String?>,
    onSnackbarMessageShown: () -> Unit
) {
    val controller = rememberColorPickerController()
    val scrollState = rememberScrollState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
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
                    .padding(bottom = spacing)
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    controller = controller,
                    initialColor = selectedColor,
                    onColorChanged = { envelope: ColorEnvelope ->
                        onColorChanged(envelope)
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }

            ModeColor(
                selectedColor = lightColor,
                brightness = lightBrightness,
                onBrightnessChange = onLightBrightnessChange,
                alpha = lightAlpha,
                onAlphaChange = onLightAlphaChange,
                textColor = lightTextColor,
                textSliderPosition = lightTextSliderPosition,
                onTextSliderChange = onLightTextSliderChange,
                isLightMode = true,
                onToggleExpanded = onToggleExpanded
            )

            ModeColor(
                selectedColor = darkColor,
                brightness = darkBrightness,
                onBrightnessChange = onDarkBrightnessChange,
                alpha = darkAlpha,
                onAlphaChange = onDarkAlphaChange,
                textColor = darkTextColor,
                textSliderPosition = darkTextSliderPosition,
                onTextSliderChange = onDarkTextSliderChange,
                isLightMode = false,
                onToggleExpanded = onToggleExpanded
            )

            MainContentContainer(
                modifier = Modifier.padding(vertical = 0.dp)
            ) {
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
            }

            Spacer(modifier = Modifier.height(spacing))

            ButtonRow(
                isValid = isColorNameValid,
                isLoading = saving,
                firstLabel = if (saving) stringResource(R.string.button_saving) else stringResource(R.string.button_save_color),
                firstOnClick = onSaveClick,
                secondLabel = stringResource(R.string.button_cancel),
                secondOnClick = onCancelClick,
            )


        }
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

@Composable
fun ModeColor(
    selectedColor: Color,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    alpha: Float,
    onAlphaChange: (Float) -> Unit,
    textColor: Color,
    textSliderPosition: Float,
    onTextSliderChange: (Float) -> Unit,
    isLightMode: Boolean,
    onToggleExpanded: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val exampleTitle = stringResource(R.string.task_example_title)
    val exampleInfo = stringResource(R.string.task_example_info)

    val exampleTask = remember(selectedColor, textColor, exampleTitle, exampleInfo) {
        val baseTask = ExampleTask(
            title = exampleTitle,
            info = exampleInfo
        ).toTask()
        val featureColor = selectedColor.toFeatureColor()
        baseTask.copy(color = featureColor)
    }

    val title = if (isLightMode) stringResource(R.string.extra_color_light_mode) else stringResource(R.string.extra_color_dark_mode)
    val backgroundColor = if (isLightMode) BackgroundGrey else DarkBackgroundGrey
    val headerTextColor = if (isLightMode) Color.Black else Color.White
    val padding = 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = padding)
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = padding),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = headerTextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.extra_color_brightness),
                    style = MaterialTheme.typography.titleSmall,
                    color = headerTextColor
                )
                Slider(
                    value = brightness,
                    onValueChange = onBrightnessChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.extra_color_alpha),
                    style = MaterialTheme.typography.titleSmall,
                    color = headerTextColor
                )
                Slider(
                    value = alpha,
                    onValueChange = onAlphaChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.extra_color_text),
                style = MaterialTheme.typography.titleSmall,
                color = headerTextColor
            )
            Slider(
                value = textSliderPosition,
                onValueChange = onTextSliderChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(horizontal = padding)
        ) {
            DayComponent(
                task = exampleTask,
                textColor = textColor,
                isExpanded = true,
                onToggleExpanded = { _ ->
                    onToggleExpanded()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                isFirst = true,
                isLast = true,
                enableInteraction = false
            )
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun CreateColorScreenPreview() {
//    val selectedColor = Color(0xFF6200EE)
//    val exampleTask = remember(selectedColor) {
//        val baseTask = ExampleTask().toTask()
//        val featureColor = selectedColor.toFeatureColor()
//        baseTask.copy(color = featureColor)
//    }
//
//    val previewSnackbarMessage = remember { MutableStateFlow<String?>(null) }
//
//    PreviewScreen {
//        CreateColorContent(
//            selectedColor = selectedColor,
//            colorName = "Example color",
//            isColorNameActive = false,
//            isColorNameValid = true,
//            saving = false,
//            onColorChanged = {},
//            onColorNameChange = {},
//            onColorNameActiveChange = {},
//            onSaveClick = {},
//            onCancelClick = {},
//            snackbarMessage = previewSnackbarMessage,
//            onSnackbarMessageShown = {},
//            onToggleExpanded = {}
//        )
//    }
//}

private fun Color.toFeatureColor(): FeatureColor {
    val red = (red * 255f).toInt().coerceIn(0, 255)
    val green = (green * 255f).toInt().coerceIn(0, 255)
    val blue = (blue * 255f).toInt().coerceIn(0, 255)
    val alpha = alpha
    val hex = "#%02X%02X%02X".format(red, green, blue)
    return FeatureColor(
        id = 0,
        name = "Preview $hex",
        lightRed = red,
        lightGreen = green,
        lightBlue = blue,
        lightAlpha = alpha,
        darkRed = red,
        darkGreen = green,
        darkBlue = blue,
        darkAlpha = alpha
    )
}
