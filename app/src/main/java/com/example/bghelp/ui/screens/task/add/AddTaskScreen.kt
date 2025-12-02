package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.ui.components.ButtonRow
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.components.ReusableSnackbarHost
import com.example.bghelp.ui.screens.task.add.color.Color
import com.example.bghelp.ui.screens.task.add.date.Date
import com.example.bghelp.ui.screens.task.add.image.Image
import com.example.bghelp.ui.screens.task.add.location.Location
import com.example.bghelp.ui.screens.task.add.note.Note
import com.example.bghelp.ui.screens.task.add.remind.Remind
import com.example.bghelp.ui.screens.task.add.repeat.Repeat
import com.example.bghelp.ui.screens.task.add.title.Title
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.dismissKeyboardOnTap
import com.example.bghelp.ui.screens.task.main.TaskNavigationKeys
import kotlinx.coroutines.delay
import java.time.ZoneId

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

    val saveFailedMessage = stringResource(R.string.task_save_failed)
    val savedTaskInfo by viewModel.savedTaskInfo.collectAsState()
    
    // Save task and pass the date and ID back through SavedStateHandle
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveTaskState.Success -> {
                savedTaskInfo?.let { (taskId, taskDate) ->
                    val taskDateMillis = taskDate.atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        TaskNavigationKeys.TASK_DATE_TO_NAVIGATE_TO,
                        taskDateMillis
                    )
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        TaskNavigationKeys.TASK_ID_TO_SCROLL_TO,
                        taskId
                    )
                }
                navController.popBackStack()
                viewModel.consumeSaveState()
            }

            is SaveTaskState.Error -> {
                viewModel.showSnackbar(saveFailedMessage)
                viewModel.consumeSaveState()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .dismissKeyboardOnTap {
                viewModel.clearNonCalendarInputSelections()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        label = stringResource(R.string.task_undo_reset),
                        imageVector = Icons.Default.Refresh,
                        onClick = { viewModel.undoResetForm() },
                        contentDescription = stringResource(R.string.task_undo_reset)
                    )
                } else {
                    FormResetComponent(
                        label = stringResource(R.string.task_reset_form),
                        imageVector = Icons.Default.Refresh,
                        onClick = { viewModel.resetForm() },
                        contentDescription = stringResource(R.string.task_reset_form)
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

                Color(
                    viewModel = viewModel,
                    navController = navController
                )

                AddTaskSpacerLarge()
                AddTaskSpacerLarge()
                AddTaskSpacerLarge()
            }

            ButtonRow(
                isValid = isValid,
                isLoading = saveState is SaveTaskState.Saving,
                firstLabel = when {
                    saveState is SaveTaskState.Saving -> stringResource(R.string.button_saving)
                    isEditing -> stringResource(R.string.button_edit_task)
                    else -> stringResource(R.string.button_save_task)
                },
                firstOnClick = {
                    if (isValid && saveState !is SaveTaskState.Saving) {
                        viewModel.saveTask()
                    } else if (!isValid && saveState !is SaveTaskState.Saving) {
                        val errorMessage = viewModel.getValidationError()
                        errorMessage?.let { viewModel.showSnackbar(it) }
                    }
                },
                secondLabel = stringResource(R.string.button_cancel),
                secondOnClick = { navController.popBackStack() },
            )
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
