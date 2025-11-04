package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.UserRemindSelection
import com.example.bghelp.ui.screens.task.add.UserSoundSelection
import com.example.bghelp.ui.screens.task.add.UserVibrateSelection
import androidx.navigation.NavController

@Composable
fun RemindSection(
    viewModel: AddTaskViewModel,
    navController: NavController
) {
    val userRemindSelection by viewModel.userRemindSelection.collectAsState()
    val remindStringChoices = remember {
        mapOf(
            UserRemindSelection.OFF to AddTaskStrings.DONT_REMIND_ME,
            UserRemindSelection.ON to AddTaskStrings.REMIND_ME
        )
    }
    val remindIconChoices = remember {
        mapOf(
            UserRemindSelection.OFF to R.drawable.dont_remind_me,
            UserRemindSelection.ON to R.drawable.remind_me
        )
    }

    val userSoundSelection by viewModel.userSoundSelection.collectAsState()
    val soundStringChoices = remember {
        mapOf(
            UserSoundSelection.OFF to AddTaskStrings.ALARM_OFF,
            UserSoundSelection.ONCE to AddTaskStrings.ALARM_ONCE,
            UserSoundSelection.CONTINUOUS to AddTaskStrings.ALARM_CONTINUOUS
        )
    }
    val soundIconChoices = remember {
        mapOf(
            UserSoundSelection.OFF to R.drawable.sound_off,
            UserSoundSelection.ONCE to R.drawable.sound_once,
            UserSoundSelection.CONTINUOUS to R.drawable.sound_continuous
        )
    }

    val userVibrateSelection by viewModel.userVibrateSelection.collectAsState()
    val vibrateStringChoices = remember {
        mapOf(
            UserVibrateSelection.OFF to AddTaskStrings.VIBRATE_OFF,
            UserVibrateSelection.ONCE to AddTaskStrings.VIBRATE_ONCE,
            UserVibrateSelection.CONTINUOUS to AddTaskStrings.VIBRATE_CONTINUOUS
        )
    }
    val vibrateIconChoices = remember {
        mapOf(
            UserVibrateSelection.OFF to R.drawable.vibrate_off,
            UserVibrateSelection.ONCE to R.drawable.vibrate_once,
            UserVibrateSelection.CONTINUOUS to R.drawable.vibrate_continuous
        )
    }

    // Remind
    AddTaskHeader(
        viewModel = viewModel,
        userSectionSelection = userRemindSelection,
        stringChoices = remindStringChoices,
        iconChoices = remindIconChoices,
        toggleSection = { viewModel.toggleRemindSelection() }
    )

    if (userRemindSelection == UserRemindSelection.ON) {
        RemindSelection(
            viewModel = viewModel
        )

        AddTaskSpacerMedium()

        // Sound
        AddTaskHeader(
            viewModel = viewModel,
            userSectionSelection = userSoundSelection,
            stringChoices = soundStringChoices,
            iconChoices = soundIconChoices,
            toggleSection = { viewModel.toggleSoundSelection() }
        )

        if (userSoundSelection != UserSoundSelection.OFF) {
            AlarmSelection(
                viewModel = viewModel,
                navController = navController,
                audioManager = viewModel.audioManager
            )
        }

        AddTaskSpacerMedium()

        // Vibrate
        AddTaskHeader(
            viewModel = viewModel,
            userSectionSelection = userVibrateSelection,
            stringChoices = vibrateStringChoices,
            iconChoices = vibrateIconChoices,
            toggleSection = { viewModel.toggleVibrateSelection() }
        )
    }
}
