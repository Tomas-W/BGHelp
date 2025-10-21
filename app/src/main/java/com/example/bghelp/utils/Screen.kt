package com.example.bghelp.utils


sealed class Screen(
    val title: String,
    val route: String
){
    object HomeScreen: Screen("Upcoming", "home_screen")

    object TaskScreen: Screen("Tasks", "task_screen")
    object AddTaskScreen: Screen("Add Task", "add_task_screen")

    object TargetScreen: Screen("Targets", "target_screen")
    object AddTargetScreen: Screen("Add Target", "add_target_screen")

    object ItemScreen: Screen("Items", "item_screen")
    object AddItemScreen: Screen("Add Item", "add_item_screen")

    object EventScreen: Screen("Events", "event_screen")
    object AddEventScreen: Screen("Add Event", "add_event_screen")

}