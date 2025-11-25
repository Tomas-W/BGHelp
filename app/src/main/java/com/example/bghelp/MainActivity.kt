package com.example.bghelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import com.example.bghelp.ui.MainScreen
import com.example.bghelp.ui.components.SystemBars
import com.example.bghelp.ui.theme.BGHelpTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
