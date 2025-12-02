package com.example.bghelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bghelp.ui.MainScreen
import com.example.bghelp.ui.components.SystemBars
import com.example.bghelp.ui.theme.BGHelpTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            BGHelpTheme {
                SystemBars(window)
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

// TODO: Custom snackbar ( info, warning, error )

// TODO: Let calendar take params for highlighting

// TODO: Swipe to navigate days with tasks

// TODO: Loading indicators

// TODO: Rename table columns for clarity and sync them around

// TODO: Back button on LocationPickerScreen

// TODO: Use Styles.kt in components?

// TODO: Highlight Task after creating/updating


