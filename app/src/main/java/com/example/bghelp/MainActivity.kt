package com.example.bghelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.bghelp.ui.MainScreen
import com.example.bghelp.ui.components.SystemBars
import com.example.bghelp.ui.theme.BGHelpTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BGHelpTheme {
                SystemBars() // Adjusts styling based on color mode
                MainScreen()
            }
        }
    }
}

// TODO: Note creation -> Note is still a string

// TODO: Deleting a Color -> Check if color is in use before running delete
// TODO                      Prompt for replacement color when so
// TODO                      Update all effected Tasks
// TODO                      Delete color

// TODO: Custom snackbars ( info, warning, error )

// TODO: Let calendar take params for highlighting

// TODO: Swipe to navigate days with tasks

// TODO: Implement strings.xml

// TODO: Loading indicators

// TODO: Rename table columns for clarity and sync them around

// TODO: Back button on LocationPickerScreen

// TODO: Open LocationPickerScreen when selecting a location on AddTaskScreen?

// TODO: Deleting multiple tasks not possible now since the undo is a singleton

// TODO: Task modal should say if task is recurring, and if so add delete all option

// TODO: Use Styles.kt in components?

// TODO: When not on feature screen, make bottomnav back button?

// TODO: Fix Color(picker) implementation

// TODO: Translate default colors?


