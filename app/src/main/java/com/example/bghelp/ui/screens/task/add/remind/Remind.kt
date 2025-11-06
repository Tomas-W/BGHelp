package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.UserRemindSelection
import com.example.bghelp.ui.screens.task.add.UserSoundSelection

@Composable
fun Remind(
    viewModel: AddTaskViewModel,
    navController: NavController
) {
    val userRemindSelection by viewModel.userRemindSelection.collectAsState()
    val userSoundSelection by viewModel.userSoundSelection.collectAsState()
    val userVibrateSelection by viewModel.userVibrateSelection.collectAsState()

    // Remind
    Header(
        viewModel = viewModel,
        userSectionSelection = userRemindSelection,
        toggleSection = { viewModel.toggleRemindSelection() }
    )
    // Before after start
    if (userRemindSelection == UserRemindSelection.ON) {
        SubContainer {
            RemindSelection(
                viewModel = viewModel
            )
        }

        AddTaskSpacerMedium()

        // Sound
        Header(
            viewModel = viewModel,
            userSectionSelection = userSoundSelection,
            toggleSection = { viewModel.toggleSoundSelection() }
        )

        if (userSoundSelection != UserSoundSelection.OFF) {
            // Alarm
            SubContainer {
                AlarmSelection(
                    viewModel = viewModel,
                    navController = navController,
                    audioManager = viewModel.audioManager
                )
            }
        }

        AddTaskSpacerMedium()

        // Vibrate
        Header(
            viewModel = viewModel,
            userSectionSelection = userVibrateSelection,
            toggleSection = { viewModel.toggleVibrateSelection() }
        )
    }
}
