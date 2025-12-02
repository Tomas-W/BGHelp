package com.example.bghelp.ui.screens.colorpicker

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.R
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.domain.model.CreateFeatureColor
import com.example.bghelp.ui.theme.TaskDefault
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateColorViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {
    // MAIN COLOR
    private val _baseColor = MutableStateFlow(TaskDefault)

    private val _selectedColor = MutableStateFlow(TaskDefault)
    val selectedColor: StateFlow<Color> = _selectedColor.asStateFlow()

    private val _createdColorId = MutableStateFlow<Int?>(null)
    val createdColorId: StateFlow<Int?> = _createdColorId.asStateFlow()

    private val _colorName = MutableStateFlow("")
    val colorName: StateFlow<String> = _colorName.asStateFlow()

    val isColorNameValid: StateFlow<Boolean> = _colorName.map { name ->
        validateColorName(name) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _isColorNameActive = MutableStateFlow(false)
    val isColorNameActive: StateFlow<Boolean> = _isColorNameActive.asStateFlow()

    fun onColorChanged(envelope: ColorEnvelope) {
        val color = envelope.color
        _baseColor.value = color
        _selectedColor.value = color
        recomputeLightColor()
        recomputeDarkColor()
    }

    fun setColorName(name: String) {
        val filtered = name.filter { it.isLetterOrDigit() || it == ' ' }
        _colorName.value = filtered
    }

    fun setColorNameActive(active: Boolean) {
        _isColorNameActive.value = active
    }

    fun clearColorNameInputSelection() {
        _isColorNameActive.value = false
    }
    // MAIN COLOR

    // LIGHT MODE
    private val _lightColor = MutableStateFlow(TaskDefault)
    val lightColor: StateFlow<Color> = _lightColor.asStateFlow()

    private val _lightBrightness = MutableStateFlow(1.0f)
    val lightBrightness: StateFlow<Float> = _lightBrightness.asStateFlow()

    private val _lightAlpha = MutableStateFlow(1.0f)
    val lightAlpha: StateFlow<Float> = _lightAlpha.asStateFlow()

    private val _lightTextColor = MutableStateFlow(Color.Black)
    val lightTextColor: StateFlow<Color> = _lightTextColor.asStateFlow()

    private val _lightTextSliderPosition = MutableStateFlow(1.0f)
    val lightTextSliderPosition: StateFlow<Float> = _lightTextSliderPosition.asStateFlow()

    fun setLightBrightness(value: Float) {
        _lightBrightness.value = value.coerceIn(0f, 1f)
        recomputeLightColor()
    }

    fun setLightAlpha(value: Float) {
        _lightAlpha.value = value.coerceIn(0f, 1f)
        recomputeLightColor()
    }

    fun setLightTextSliderPosition(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        _lightTextSliderPosition.value = clamped
        val grey = 1f - clamped
        _lightTextColor.value = Color(grey, grey, grey, 1.0f)
    }
    // LIGHT MODE

    // DARK MODE
    private val _darkColor = MutableStateFlow(TaskDefault)
    val darkColor: StateFlow<Color> = _darkColor.asStateFlow()

    private val _darkBrightness = MutableStateFlow(1.0f)
    val darkBrightness: StateFlow<Float> = _darkBrightness.asStateFlow()

    private val _darkAlpha = MutableStateFlow(1.0f)
    val darkAlpha: StateFlow<Float> = _darkAlpha.asStateFlow()

    private val _darkTextColor = MutableStateFlow(Color.Black)
    val darkTextColor: StateFlow<Color> = _darkTextColor.asStateFlow()

    private val _darkTextSliderPosition = MutableStateFlow(1.0f)
    val darkTextSliderPosition: StateFlow<Float> = _darkTextSliderPosition.asStateFlow()

    fun setDarkBrightness(value: Float) {
        _darkBrightness.value = value.coerceIn(0f, 1f)
        recomputeDarkColor()
    }

    fun setDarkAlpha(value: Float) {
        _darkAlpha.value = value.coerceIn(0f, 1f)
        recomputeDarkColor()
    }

    fun setDarkTextSliderPosition(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        _darkTextSliderPosition.value = clamped
        val grey = 1f - clamped
        _darkTextColor.value = Color(grey, grey, grey, 1.0f)
    }
    // DARK MODE

    // SNACKBAR
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    // SNACKBAR

    // SAVING
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun saveColor() {
        if (_saving.value) return
        
        val nameError = validateColorName(_colorName.value)
        if (nameError != null) {
            showSnackbar(nameError)
            return
        }

        val light = _lightColor.value
        val dark = _darkColor.value
        val lightText = _lightTextColor.value
        val darkText = _darkTextColor.value

        val lightRed = (light.red * 255f).toInt().coerceIn(0, 255)
        val lightGreen = (light.green * 255f).toInt().coerceIn(0, 255)
        val lightBlue = (light.blue * 255f).toInt().coerceIn(0, 255)
        val lightAlpha = light.alpha

        val darkRed = (dark.red * 255f).toInt().coerceIn(0, 255)
        val darkGreen = (dark.green * 255f).toInt().coerceIn(0, 255)
        val darkBlue = (dark.blue * 255f).toInt().coerceIn(0, 255)
        val darkAlpha = dark.alpha

        val lightTextRed = (lightText.red * 255f).toInt().coerceIn(0, 255)
        val lightTextGreen = (lightText.green * 255f).toInt().coerceIn(0, 255)
        val lightTextBlue = (lightText.blue * 255f).toInt().coerceIn(0, 255)
        val lightTextAlpha = lightText.alpha

        val darkTextRed = (darkText.red * 255f).toInt().coerceIn(0, 255)
        val darkTextGreen = (darkText.green * 255f).toInt().coerceIn(0, 255)
        val darkTextBlue = (darkText.blue * 255f).toInt().coerceIn(0, 255)
        val darkTextAlpha = darkText.alpha

        _saving.value = true
        viewModelScope.launch {
            try {
                val colorId = colorRepository.addColor(
                    CreateFeatureColor(
                        name = _colorName.value.trim(),
                        isDefault = false,
                        lightRed = lightRed,
                        lightGreen = lightGreen,
                        lightBlue = lightBlue,
                        lightAlpha = lightAlpha,
                        darkRed = darkRed,
                        darkGreen = darkGreen,
                        darkBlue = darkBlue,
                        darkAlpha = darkAlpha,
                        lightTextRed = lightTextRed,
                        lightTextGreen = lightTextGreen,
                        lightTextBlue = lightTextBlue,
                        lightTextAlpha = lightTextAlpha,
                        darkTextRed = darkTextRed,
                        darkTextGreen = darkTextGreen,
                        darkTextBlue = darkTextBlue,
                        darkTextAlpha = darkTextAlpha
                    )
                ).toInt()
                _createdColorId.value = colorId
                _saved.value = true
            } finally {
                _saving.value = false
            }
        }
    }
    // SAVING

    // MISC
    private fun adjustedColor(base: Color, brightness: Float, alpha: Float): Color {
        val b = brightness.coerceIn(0f, 1f)
        val a = alpha.coerceIn(0f, 1f)
        return Color(
            red = (base.red * b).coerceIn(0f, 1f),
            green = (base.green * b).coerceIn(0f, 1f),
            blue = (base.blue * b).coerceIn(0f, 1f),
            alpha = a
        )
    }

    private fun recomputeLightColor() {
        _lightColor.value = adjustedColor(_baseColor.value, _lightBrightness.value, _lightAlpha.value)
    }

    private fun recomputeDarkColor() {
        _darkColor.value = adjustedColor(_baseColor.value, _darkBrightness.value, _darkAlpha.value)
    }

    private fun validateColorName(name: String): String? {
        if (name.length < 3) {
            return appContext.getString(R.string.snackbar_color_name_length)
        }
        if (!name.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
            return appContext.getString(R.string.snackbar_color_name_regex)
        }
        return null
    }

}