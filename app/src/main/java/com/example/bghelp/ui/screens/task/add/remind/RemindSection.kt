package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.utils.AudioManager
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskHeader
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerMedium
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.UserRemindSelection
import com.example.bghelp.ui.screens.task.add.UserSoundSelection
import com.example.bghelp.ui.screens.task.add.UserVibrateSelection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

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
    AddTaskSpacerMedium()

    RemindSelection(
        viewModel = viewModel,
        userRemindSelection = userRemindSelection
    )

    AddTaskSpacerMedium()

    if (userRemindSelection == UserRemindSelection.ON) {
        // Sound
        AddTaskHeader(
            viewModel = viewModel,
            userSectionSelection = userSoundSelection,
            stringChoices = soundStringChoices,
            iconChoices = soundIconChoices,
            toggleSection = { viewModel.toggleSoundSelection() }
        )
        if (userSoundSelection != UserSoundSelection.OFF) {
            AddTaskSpacerMedium()

            val selectedAudioFile by viewModel.selectedAudioFile.collectAsState()
            
            Row {
                Spacer(modifier = Modifier.width(2 * Sizes.Icon.Large))

                AlarmDropdown(
                    selectedAudioFile = selectedAudioFile,
                    onAudioFileSelected = { fileName ->
                        viewModel.setSelectedAudioFile(fileName)
                    },
                    navController = navController,
                    audioManager = viewModel.audioManager
                )
            }

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

@Composable
fun AlarmDropdown(
    selectedAudioFile: String,
    onAudioFileSelected: (String) -> Unit,
    navController: NavController,
    audioManager: AudioManager,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    val defaultAudioFiles = remember { audioManager.getDefaultAudioFiles() }
    val userRecordings = remember { audioManager.getUserRecordings() }
    
    val defaultAudioFileLabels = remember(defaultAudioFiles) {
        defaultAudioFiles.associateWith { fileName ->
            audioManager.getAudioFileLabel(fileName)
        }
    }
    
    val userRecordingLabels = remember(userRecordings) {
        userRecordings.associateWith { filePath ->
            audioManager.getAudioFileLabel(filePath, isFullPath = true)
        }
    }
    
    val dropdownHeight = (6 * UI.DROPDOWN_ITEM_HEIGHT).dp
    
    val displayText = remember(selectedAudioFile, defaultAudioFileLabels, userRecordingLabels) {
        if (selectedAudioFile.isEmpty()) {
            AddTaskStrings.SELECT_ALARM
        } else {
            userRecordingLabels[selectedAudioFile] 
                ?: defaultAudioFileLabels[selectedAudioFile] 
                ?: audioManager.getAudioFileLabel(selectedAudioFile, isFullPath = true)
        }
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
            .padding(end = AddTaskConstants.END_PADDING.dp)
    ) {
        Text(
            text = displayText,
            style = TextStyles.Default.Medium,
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onDropdownClick()
                    isExpanded = true
                }
        )

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiary)
                .heightIn(max = dropdownHeight)
                .wrapContentWidth(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            offset = DpOffset(x = 0.dp, y = 4.dp)
        ) {
            DropdownItem(
                label = AddTaskStrings.SELECT_ALARM,
                onClick = {
                    onAudioFileSelected("")
                    isExpanded = false
                },
                textStyle = TextStyles.Default.Small
            )
            
            DropdownItem(
                label = AddTaskStrings.CREATE_ALARM,
                onClick = {
                    navController.navigate(Screen.Options.CreateAlarm.route) {
                        launchSingleTop = true
                    }
                    isExpanded = false
                },
                textStyle = TextStyles.Default.Small
            )
            
            userRecordings.forEach { filePath ->
                DropdownItem(
                    label = userRecordingLabels[filePath] ?: audioManager.getAudioFileLabel(filePath, isFullPath = true),
                    onClick = {
                        onAudioFileSelected(filePath)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.Small
                )
            }
            
            defaultAudioFiles.forEach { fileName ->
                DropdownItem(
                    label = defaultAudioFileLabels[fileName] ?: fileName,
                    onClick = {
                        onAudioFileSelected(fileName)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.Small
                )
            }
        }
    }
}
