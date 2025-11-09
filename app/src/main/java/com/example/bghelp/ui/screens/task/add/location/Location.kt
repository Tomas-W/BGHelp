package com.example.bghelp.ui.screens.task.add.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.locationpicker.LocationNavigationKeys
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.screens.task.add.UserLocationSelection
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.selectedStyle
import com.example.bghelp.ui.theme.Sizes
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import java.util.ArrayList

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
            if (entry.destination.route == Screen.Tasks.Add.route) {
                val result = entry.savedStateHandle
                    .get<ArrayList<TaskLocation>>(LocationNavigationKeys.RESULT)
                if (result != null) {
                    if (allowMultiple) {
                        viewModel.appendSelectedLocations(result)
                    } else {
                        viewModel.setSelectedLocations(result)
                    }
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
            LocationContent(
                locations = selectedLocations,
                onAddLocation = {
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

@Composable
private fun LocationContent(
    locations: List<TaskLocation>,
    onAddLocation: () -> Unit,
    onRemoveLocation: (TaskLocation) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.Small)
    ) {
        Text(
            modifier = Modifier
                .clickable { onAddLocation() },
            text = "Add location",
            style = deselectedStyle
        )

        locations.forEach { location ->
            HorizontalDivider()
            LocationSummary(
                location = location,
                onRemove = { onRemoveLocation(location) }
            )
        }
    }
}

@Composable
private fun LocationSummary(
    location: TaskLocation,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = location.name.ifBlank { "unnamed" },
                style = selectedStyle
            )

            Text(
                text = location.address,
                style = deselectedStyle
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.size(Sizes.Icon.Large),
                onClick = onRemove
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Delete address"
                )
            }
        }
    }
//
//    Column {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = location.name.ifBlank { "unnamed" },
//                style = selectedStyle
//            )
//
//            IconButton(
//                modifier = Modifier.size(Sizes.Icon.Medium),
//                onClick = onRemove
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.delete),
//                    contentDescription = "Delete address"
//                )
//            }
//        }
//
//        Text(
//            text = location.address,
//            style = deselectedStyle
//        )
//    }
}

private fun formatCoordinate(value: Double): String =
    String.format(Locale.getDefault(), "%.5f", value)