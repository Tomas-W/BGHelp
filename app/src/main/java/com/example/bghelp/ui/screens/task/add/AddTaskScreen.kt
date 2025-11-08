package com.example.bghelp.ui.screens.task.add
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.screens.task.add.title.TitleSection
import com.example.bghelp.ui.screens.task.add.date.DateSection
import com.example.bghelp.ui.screens.task.add.remind.Remind
import com.example.bghelp.ui.screens.task.add.color.ColorSection
import com.example.bghelp.ui.screens.task.add.repeat.Repeat
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.dismissKeyboardOnTap

 

@Composable
fun AddTaskScreen(
    navController: NavController,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap {
                viewModel.clearAllInputSelections()
            }
    ) {
        MainContentContainer(modifier = Modifier.verticalScroll(rememberScrollState())) {
            AddTaskSpacerLarge()

            TitleSection(viewModel = viewModel)

            AddTaskDivider()

            DateSection(viewModel = viewModel)

            AddTaskDivider()

            Repeat(viewModel = viewModel)

            AddTaskDivider()

            Remind(
                viewModel = viewModel,
                navController = navController
            )

            AddTaskDivider()

            ColorSection(viewModel = viewModel)
        }
    }
}

@Composable
fun AddTaskSpacerLarge() {
    Spacer(modifier = Modifier.height(Sizes.Size.Medium))
}

@Composable
fun AddTaskSpacerMedium() {
    Spacer(modifier = Modifier.height(Sizes.Size.Small))
}

@Composable
fun AddTaskSpacerSmall() {
    Spacer(modifier = Modifier.height(Sizes.Size.ExtraSmall))
}

@Composable
fun AddTaskDivider() {
    AddTaskSpacerLarge()
    HorizontalDivider()
    AddTaskSpacerLarge()
}
