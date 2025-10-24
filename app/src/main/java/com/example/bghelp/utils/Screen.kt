package com.example.bghelp.utils

sealed class Screen(
    val title: String,
    val route: String
) {
    // Tasks Feature Screens
    sealed class Tasks(title: String, route: String) : Screen(title, route) {
        object Home : Tasks("Tasks", "tasks")
        object Add : Tasks("Add Task", "tasks/add")
        object Calendar : Tasks("Tasks Calendar", "tasks/calendar")
        object Wallpaper : Tasks("Tasks Wallpaper", "tasks/wallpaper")
    }
    
    // Targets Feature Screens
    sealed class Targets(title: String, route: String) : Screen(title, route) {
        object Home : Targets("Targets", "targets")
        object Add : Targets("Add Target", "targets/add")
        object Calendar : Targets("Targets Calendar", "targets/calendar")
        object Wallpaper : Targets("Targets Wallpaper", "targets/wallpaper")
    }
    
    // Items Feature Screens
    sealed class Items(title: String, route: String) : Screen(title, route) {
        object Home : Items("Items", "items")
        object Add : Items("Add Item", "items/add")
        object Calendar : Items("Items Calendar", "items/calendar")
        object Wallpaper : Items("Items Wallpaper", "items/wallpaper")
    }
    
    // Events Feature Screens
    sealed class Events(title: String, route: String) : Screen(title, route) {
        object Home : Events("Events", "events")
        object Add : Events("Add Event", "events/add")
        object Calendar : Events("Events Calendar", "events/calendar")
        object Wallpaper : Events("Events Wallpaper", "events/wallpaper")
    }
    
    // Options Feature Screens (renamed from Settings)
    sealed class Options(title: String, route: String) : Screen(title, route) {
        object Settings : Options("Settings", "options/settings")
        object CreateAlarm : Options("Create Alarm", "options/create_alarm")
    }
    
    companion object {
        fun getScreenByRoute(route: String?): Screen {
            return when (route) {
                Tasks.Home.route -> Tasks.Home
                Tasks.Add.route -> Tasks.Add
                Tasks.Calendar.route -> Tasks.Calendar
                Tasks.Wallpaper.route -> Tasks.Wallpaper
                Targets.Home.route -> Targets.Home
                Targets.Add.route -> Targets.Add
                Targets.Calendar.route -> Targets.Calendar
                Targets.Wallpaper.route -> Targets.Wallpaper
                Items.Home.route -> Items.Home
                Items.Add.route -> Items.Add
                Items.Calendar.route -> Items.Calendar
                Items.Wallpaper.route -> Items.Wallpaper
                Events.Home.route -> Events.Home
                Events.Add.route -> Events.Add
                Events.Calendar.route -> Events.Calendar
                Events.Wallpaper.route -> Events.Wallpaper
                Options.Settings.route -> Options.Settings
                Options.CreateAlarm.route -> Options.CreateAlarm
                else -> Tasks.Home // Default to Tasks instead of Empty
            }
        }

        val bottomNavScreens = listOf(
            Tasks.Home,
            Targets.Home,
            Items.Home,
            Events.Home
        )

        val addScreens = listOf(
            Tasks.Add,
            Targets.Add,
            Items.Add,
            Events.Add
        )

        val calendarScreens = listOf(
            Tasks.Calendar,
            Targets.Calendar,
            Items.Calendar,
            Events.Calendar
        )

        val wallpaperScreens = listOf(
            Tasks.Wallpaper,
            Targets.Wallpaper,
            Items.Wallpaper,
            Events.Wallpaper
        )

        val optionsScreens = listOf(
            Options.Settings,
            Options.CreateAlarm
        )

        val featureHomes = listOf(
            Tasks.Home,
            Targets.Home,
            Items.Home,
            Events.Home
        )
    }
}