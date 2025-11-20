package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import com.example.bghelp.ui.components.CancelButton
import com.example.bghelp.ui.components.ConfirmButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.ReusableSnackbarHost
import com.example.bghelp.ui.screens.task.add.title.Title
import com.example.bghelp.ui.screens.task.add.date.Date
import com.example.bghelp.ui.screens.task.add.remind.Remind
import com.example.bghelp.ui.screens.task.add.color.Color
import com.example.bghelp.ui.screens.task.add.repeat.Repeat
import com.example.bghelp.ui.screens.task.add.location.Location
import com.example.bghelp.ui.screens.task.add.image.Image
import com.example.bghelp.ui.screens.task.add.note.Note
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.dismissKeyboardOnTap

@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: AddTaskViewModel,
    taskId: Int? = null
) {
    val saveState by viewModel.saveState.collectAsState()
    val isValid by viewModel.isNewTaskValid.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val scrollState = rememberScrollState()
    
    // Load task for editing if taskId is provided
    LaunchedEffect(taskId) {
        taskId?.let { id ->
            if (id > 0) {
                viewModel.loadTaskForEditingById(id)
            }
        }
    }

    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveTaskState.Success -> {
                navController.popBackStack()
                viewModel.consumeSaveState()
            }
            is SaveTaskState.Error -> {
                viewModel.showSnackbar(AddTaskStrings.ERROR_SAVE_FAILED)
                viewModel.consumeSaveState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap {
                viewModel.clearNonCalendarInputSelections()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MainContentContainer(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .verticalScroll(scrollState)
            ) {
                val showUndoReset by viewModel.showUndoResetForm.collectAsState()
                
                LaunchedEffect(showUndoReset) {
                    if (showUndoReset) {
                        delay(5000)
                        viewModel.hideUndoResetForm()
                        viewModel.cleanUpResetState()
                    }
                }

                // Reset / Undo reset
                if (showUndoReset) {
                    FormResetComponent(
                        label = AddTaskStrings.UNDO_RESET_FORM,
                        imageVector = Icons.Default.Refresh,
                        onClick = { viewModel.undoResetForm() },
                        contentDescription = AddTaskStrings.UNDO_RESET_FORM_DESC
                    )
                } else {
                    FormResetComponent(
                        label = AddTaskStrings.RESET_FORM,
                        imageVector = Icons.Default.Refresh,
                        onClick = { viewModel.resetForm() },
                        contentDescription = AddTaskStrings.UNDO_RESET_FORM_DESC
                    )
                }

                Title(viewModel = viewModel)

                AddTaskDivider()

                Date(viewModel = viewModel)

                AddTaskDivider()

                Repeat(viewModel = viewModel)

                AddTaskDivider()

                Remind(
                    viewModel = viewModel,
                    navController = navController
                )

                AddTaskDivider()

                Note(
                    viewModel = viewModel,
                    navController = navController
                )

                AddTaskDivider()

                Location(
                    viewModel = viewModel,
                    navController = navController,
                    allowMultiple = true
                )

                AddTaskDivider()

                Image(
                    viewModel = viewModel
                )

                AddTaskDivider()

                Color(viewModel = viewModel)

                AddTaskSpacerLarge()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CancelButton(
                    text = AddTaskStrings.CANCEL,
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                )
                ConfirmButton(
                    text = when {
                        saveState is SaveTaskState.Saving -> AddTaskStrings.SAVING
                        isEditing -> AddTaskStrings.EDIT_TASK
                        else -> AddTaskStrings.SAVE_TASK
                    },
                    onClick = { viewModel.saveTask() },
                    modifier = Modifier.weight(1f),
                    enabled = isValid && saveState !is SaveTaskState.Saving
                )
            }
        }

        // Snackbar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ReusableSnackbarHost(
                snackbarMessage = viewModel.snackbarMessage,
                onMessageShown = { viewModel.clearSnackbarMessage() }
            )
        }
    }
}

@Composable
fun AddTaskSpacerLarge() {
    Spacer(modifier = Modifier.height(Sizes.Size.M))
}

@Composable
fun AddTaskSpacerMedium() {
    Spacer(modifier = Modifier.height(Sizes.Size.S))
}

@Composable
fun AddTaskSpacerSmall() {
    Spacer(modifier = Modifier.height(Sizes.Size.XS))
}

@Composable
fun AddTaskDivider() {
    AddTaskSpacerLarge()
    HorizontalDivider()
    AddTaskSpacerLarge()
}
