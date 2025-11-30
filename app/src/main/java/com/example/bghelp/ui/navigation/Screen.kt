package com.example.bghelp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.bghelp.R

sealed class Screen(
    val title: String,
    val route: String
) {
    // Tasks Feature Screens
    sealed class Tasks(title: String, route: String) : Screen(title, route) {
        object Main : Tasks("Tasks", "tasks")
        object Add : Tasks("Add Task", "tasks/add")
    }
    
    // Targets Feature Screens
    sealed class Targets(title: String, route: String) : Screen(title, route) {
        object Main : Targets("Targets", "targets")
        object Add : Targets("Add Target", "targets/add")
    }
    
    // Items Feature Screens
    sealed class Items(title: String, route: String) : Screen(title, route) {
        object Main : Items("Items", "items")
        object Add : Items("Add Item", "items/add")
    }
    
    // Notes Feature Screens
    sealed class Notes(title: String, route: String) : Screen(title, route) {
        object Main : Notes("Notes", "notes")
        object Add : Notes("Add Note", "notes/add")
    }
    
    // Options Feature Screens (renamed from Settings)
    sealed class Options(title: String, route: String) : Screen(title, route) {
        object Settings : Options("Settings", "options/settings")
        object CreateAlarm : Options("Create Alarm", "options/create_alarm")
        object ColorPicker : Options("Create color", "options/color_picker")
    }
    
    // Stand alone screens
    object LocationPicker : Screen("Location Picker", "location_picker") {
        const val ALLOW_MULTIPLE_ARG = "allowMultiple"
        val ROUTE_WITH_ARGS = "$route?$ALLOW_MULTIPLE_ARG={$ALLOW_MULTIPLE_ARG}"
        fun buildRoute(allowMultiple: Boolean) = "$route?$ALLOW_MULTIPLE_ARG=$allowMultiple"
    }

    
    companion object {
        fun getScreenByRoute(route: String?): Screen {
            return when (route) {
                Tasks.Main.route -> Tasks.Main
                Tasks.Add.route -> Tasks.Add
                Targets.Main.route -> Targets.Main
                Targets.Add.route -> Targets.Add
                Items.Main.route -> Items.Main
                Items.Add.route -> Items.Add
                Notes.Main.route -> Notes.Main
                Notes.Add.route -> Notes.Add
                Options.Settings.route -> Options.Settings
                Options.CreateAlarm.route -> Options.CreateAlarm
                Options.ColorPicker.route -> Options.ColorPicker
                LocationPicker.route -> LocationPicker
                else -> when {
                    route?.startsWith(LocationPicker.route) == true -> LocationPicker
                    else -> Tasks.Main // Default to Tasks
                }
            }
        }

        val taskScreens = listOf(
            Tasks.Main.route,
            Tasks.Add.route
        )

        val targetScreens = listOf(
            Targets.Main.route,
            Targets.Add.route
        )

        val itemScreens = listOf(
            Items.Main.route,
            Items.Add.route
        )

        val noteScreens = listOf(
            Notes.Main.route,
            Notes.Add.route
        )

        val optionsScreens = listOf(
            Options.Settings,
            Options.CreateAlarm,
            Options.ColorPicker,
        )

        val noBottomNavScreens = listOf(
            LocationPicker,
            Options.ColorPicker
        )
    }
}

@Composable
fun Screen.getLocalizedTitle(): String {
    return when (this) {
        is Screen.Tasks.Main -> stringResource(R.string.task_title)
        is Screen.Tasks.Add -> stringResource(R.string.task_add_title)
        is Screen.Targets.Main -> stringResource(R.string.target_title)
        is Screen.Targets.Add -> stringResource(R.string.target_add_title)
        is Screen.Items.Main -> stringResource(R.string.item_title)
        is Screen.Items.Add -> stringResource(R.string.item_add_title)
        is Screen.Notes.Main -> stringResource(R.string.note_title)
        is Screen.Notes.Add -> stringResource(R.string.note_add_title)
        is Screen.Options.Settings -> stringResource(R.string.extra_settings_title)
        is Screen.Options.CreateAlarm -> stringResource(R.string.extra_create_alarm_title)
        is Screen.Options.ColorPicker -> stringResource(R.string.extra_color_picker_title)
        is Screen.LocationPicker -> stringResource(R.string.extra_location_picker_title)
    }
}
