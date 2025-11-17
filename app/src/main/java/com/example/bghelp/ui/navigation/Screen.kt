package com.example.bghelp.ui.navigation

sealed class Screen(
    val title: String,
    val route: String
) {
    // Home Feature Screens
    sealed class Home(title: String, route: String) : Screen(title, route) {
        object Main : Home("Home", "home")
        object Wallpaper : Home("Home Wallpaper", "home/wallpaper")
    }

    // Tasks Feature Screens
    sealed class Tasks(title: String, route: String) : Screen(title, route) {
        object Main : Tasks("Tasks", "tasks")
        object Add : Tasks("Add Task", "tasks/add")
        object Calendar : Tasks("Tasks Calendar", "tasks/calendar")
        object Wallpaper : Tasks("Tasks Wallpaper", "tasks/wallpaper")
    }
    
    // Targets Feature Screens
    sealed class Targets(title: String, route: String) : Screen(title, route) {
        object Main : Targets("Targets", "targets")
        object Add : Targets("Add Target", "targets/add")
        object Calendar : Targets("Targets Calendar", "targets/calendar")
        object Wallpaper : Targets("Targets Wallpaper", "targets/wallpaper")
    }
    
    // Items Feature Screens
    sealed class Items(title: String, route: String) : Screen(title, route) {
        object Main : Items("Items", "items")
        object Add : Items("Add Item", "items/add")
        object Calendar : Items("Items Calendar", "items/calendar")
        object Wallpaper : Items("Items Wallpaper", "items/wallpaper")
    }
    
    // Notes Feature Screens
    sealed class Notes(title: String, route: String) : Screen(title, route) {
        object Main : Notes("Notes", "notes")
        object Add : Notes("Add Note", "notes/add")
        object Calendar : Notes("Notes Calendar", "notes/calendar")
        object Wallpaper : Notes("Notes Wallpaper", "notes/wallpaper")
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
                // Home.Main.route -> Home.Main
                // Home.Wallpaper.route -> Home.Wallpaper
                Tasks.Main.route -> Tasks.Main
                Tasks.Add.route -> Tasks.Add
                Tasks.Calendar.route -> Tasks.Calendar
                Tasks.Wallpaper.route -> Tasks.Wallpaper
                Targets.Main.route -> Targets.Main
                Targets.Add.route -> Targets.Add
                Targets.Calendar.route -> Targets.Calendar
                Targets.Wallpaper.route -> Targets.Wallpaper
                Items.Main.route -> Items.Main
                Items.Add.route -> Items.Add
                Items.Calendar.route -> Items.Calendar
                Items.Wallpaper.route -> Items.Wallpaper
                Notes.Main.route -> Notes.Main
                Notes.Add.route -> Notes.Add
                Notes.Calendar.route -> Notes.Calendar
                Notes.Wallpaper.route -> Notes.Wallpaper
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

        val homeScreens = listOf(
            Home.Main.route,
            Home.Wallpaper.route
        )

        val taskScreens = listOf(
            Tasks.Main.route,
            Tasks.Add.route,
            Tasks.Wallpaper.route,
            Tasks.Calendar.route
        )

        val targetScreens = listOf(
            Targets.Main.route,
            Targets.Add.route,
            Targets.Wallpaper.route,
            Targets.Calendar.route
        )

        val itemScreens = listOf(
            Items.Main.route,
            Items.Add.route,
            Items.Wallpaper.route,
            Items.Calendar.route
        )

        val noteScreens = listOf(
            Notes.Main.route,
            Notes.Add.route,
            Notes.Wallpaper.route,
            Notes.Calendar.route
        )

        val bottomNavScreens = listOf(
            Tasks.Main,
            Targets.Main,
            Items.Main,
            Notes.Main
        )

        val addScreens = listOf(
            Tasks.Add,
            Targets.Add,
            Items.Add,
            Notes.Add
        )

        val calendarScreens = listOf(
            Tasks.Calendar,
            Targets.Calendar,
            Items.Calendar,
            Notes.Calendar
        )

        val wallpaperScreens = listOf(
            Home.Wallpaper,
            Tasks.Wallpaper,
            Targets.Wallpaper,
            Items.Wallpaper,
            Notes.Wallpaper
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

        val featureMains = listOf(
            Tasks.Main,
            Targets.Main,
            Items.Main,
            Notes.Main
        )
    }
}
