package com.example.bghelp.ui.screens.task.add
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bghelp.ui.components.MainContentContainer
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
    viewModel: AddTaskViewModel
) {
    val saveState by viewModel.saveState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(saveState) {
        if (saveState is AddTaskViewModel.SaveTaskState.Success) {
            navController.popBackStack()
            viewModel.consumeSaveState()
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
                AddTaskSpacerLarge()

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
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.saveTask() },
                    enabled = saveState != AddTaskViewModel.SaveTaskState.Saving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (saveState == AddTaskViewModel.SaveTaskState.Saving) {
                            "Saving..."
                        } else {
                            "Save Task"
                        }
                    )
                }
            }
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
