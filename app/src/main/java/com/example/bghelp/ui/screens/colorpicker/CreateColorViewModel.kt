package com.example.bghelp.ui.screens.colorpicker

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.R
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.domain.model.CreateFeatureColor
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
    private val _selectedColor = MutableStateFlow(Color(0xFF2196F3))
    val selectedColor: StateFlow<Color> = _selectedColor.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _createdColorId = MutableStateFlow<Int?>(null)
    val createdColorId: StateFlow<Int?> = _createdColorId.asStateFlow()

    private val _isExampleExpanded = MutableStateFlow(false)
    val isExampleExpanded: StateFlow<Boolean> = _isExampleExpanded.asStateFlow()

    private val _colorName = MutableStateFlow("")
    val colorName: StateFlow<String> = _colorName.asStateFlow()

    private val _isColorNameActive = MutableStateFlow(false)
    val isColorNameActive: StateFlow<Boolean> = _isColorNameActive.asStateFlow()

    val isColorNameValid: StateFlow<Boolean> = _colorName.map { name ->
        validateColorName(name) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
    
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun onColorChanged(envelope: ColorEnvelope) {
        _selectedColor.value = envelope.color
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

    private fun validateColorName(name: String): String? {
        if (name.length < 3) {
            return appContext.getString(R.string.snackbar_color_name_length)
        }
        if (!name.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
            return appContext.getString(R.string.snackbar_color_name_regex)
        }
        return null
    }

    fun saveColor() {
        if (_saving.value) return
        
        val nameError = validateColorName(_colorName.value)
        if (nameError != null) {
            showSnackbar(nameError)
            return
        }

        val composeColor = _selectedColor.value
        val red = (composeColor.red * 255f).toInt().coerceIn(0, 255)
        val green = (composeColor.green * 255f).toInt().coerceIn(0, 255)
        val blue = (composeColor.blue * 255f).toInt().coerceIn(0, 255)
        // Always save with 0.4 alpha
        val alpha = 0.25f

        _saving.value = true
        viewModelScope.launch {
            try {
                val colorId = colorRepository.addColor(
                    CreateFeatureColor(
                        name = _colorName.value.trim(),
                        red = red,
                        green = green,
                        blue = blue,
                        alpha = alpha,
                        isDefault = false
                    )
                ).toInt()
                _createdColorId.value = colorId
                _saved.value = true
            } finally {
                _saving.value = false
            }
        }
    }

    fun toggleExpandExample() {
        _isExampleExpanded.value = !_isExampleExpanded.value
    }
}