package com.example.bghelp.ui.screens.task.add.remind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bghelp.utils.AudioManager
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun AlarmSelection(
    viewModel: AddTaskViewModel,
    navController: NavController,
    audioManager: AudioManager
) {
    val selectedAudioFile by viewModel.selectedAudioFile.collectAsState()
    Row {
        AlarmDropdown(
            selectedAudioFile = selectedAudioFile,
            onAudioFileSelected = { fileName ->
                viewModel.setSelectedAudioFile(fileName)
            },
            navController = navController,
            audioManager = audioManager
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
    val displayText = remember(selectedAudioFile, defaultAudioFileLabels, userRecordingLabels) {
        if (selectedAudioFile.isEmpty()) {
            STR.SELECT_ALARM
        } else {
            userRecordingLabels[selectedAudioFile] 
                ?: defaultAudioFileLabels[selectedAudioFile] 
                ?: audioManager.getAudioFileLabel(selectedAudioFile, isFullPath = true)
        }
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
    ) {
        Text(
            text = displayText,
            style = deselectedStyle,
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onDropdownClick()
                    isExpanded = true
                }
        )

        CustomDropdown(
            modifier = Modifier
                .wrapContentWidth(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownItem(
                label = STR.SELECT_ALARM,
                onClick = {
                    onAudioFileSelected("")
                    isExpanded = false
                },
                textStyle = TextStyles.Default.S,
                spacing = Sizes.Icon.XXL
            )
            DropdownItem(
                label = STR.CREATE_ALARM,
                onClick = {
                    navController.navigate(Screen.Options.CreateAlarm.route) {
                        launchSingleTop = true
                    }
                    isExpanded = false
                },
                textStyle = TextStyles.Default.S,
                spacing = Sizes.Icon.XXL
            )
            userRecordings.forEach { filePath ->
                DropdownItem(
                    label = userRecordingLabels[filePath] ?: audioManager.getAudioFileLabel(filePath, isFullPath = true),
                    onClick = {
                        onAudioFileSelected(filePath)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.S,
                    spacing = Sizes.Icon.XXL
                )
            }
            defaultAudioFiles.forEach { fileName ->
                DropdownItem(
                    label = defaultAudioFileLabels[fileName] ?: fileName,
                    onClick = {
                        onAudioFileSelected(fileName)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.S,
                    spacing = Sizes.Icon.XXL
                )
            }
        }
    }
}
