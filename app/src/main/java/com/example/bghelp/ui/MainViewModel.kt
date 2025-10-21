package com.example.bghelp.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.bghelp.utils.Screen


class MainViewModel: ViewModel() {

    private val _currentScreen: MutableState<Screen> = mutableStateOf(Screen.TaskScreen)
    val currentScreen: MutableState<Screen>
        get() = _currentScreen

    fun setCurrentScreen(screen: Screen) {
        _currentScreen.value = screen
    }

}