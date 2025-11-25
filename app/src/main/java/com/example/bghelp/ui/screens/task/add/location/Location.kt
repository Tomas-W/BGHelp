package com.example.bghelp.ui.screens.task.add.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.locationpicker.LocationNavigationKeys
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.screens.task.add.UserLocationSelection
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Location(
    viewModel: AddTaskViewModel,
    navController: NavController,
    allowMultiple: Boolean = false
) {
    val userLocationSelection by viewModel.userLocationSelection.collectAsState()
    val selectedLocations by viewModel.selectedLocations.collectAsState()

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collectLatest { entry ->
            val route = entry.destination.route
            if (route != null && route.startsWith(Screen.Tasks.Add.route)) {
                val result = entry.savedStateHandle
                    .get<ArrayList<TaskLocation>>(LocationNavigationKeys.RESULT)
                if (result != null) {
                    viewModel.setSelectedLocations(result)
                    entry.savedStateHandle.remove<ArrayList<TaskLocation>>(LocationNavigationKeys.RESULT)
                }
            }
        }
    }

    Header(
        viewModel = viewModel,
        userSectionSelection = userLocationSelection,
        toggleSection = { viewModel.toggleLocationSelection() }
    )

    if (userLocationSelection == UserLocationSelection.ON) {
        SubContainer {
            LocationSelection(
                locations = selectedLocations,
                onAddLocation = {
                    // Send the current selected locations to the location picker
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            LocationNavigationKeys.INITIAL_LOCATIONS,
                            ArrayList(selectedLocations)
                        )
                    navController.navigate(
                        Screen.LocationPicker.buildRoute(allowMultiple = allowMultiple)
                    ) {
                        launchSingleTop = true
                    }
                },
                onRemoveLocation = viewModel::removeSelectedLocation
            )
        }
    }
}
