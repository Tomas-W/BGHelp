package com.example.bghelp.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.ui.screens.note.AddNoteModal
import com.example.bghelp.ui.screens.note.AddNoteViewModel
import com.example.bghelp.ui.screens.note.NoteScreen
import com.example.bghelp.ui.screens.note.NoteWallpaperScreen
import com.example.bghelp.ui.screens.items.ItemScreen
import com.example.bghelp.ui.screens.items.ItemWallpaperScreen
import com.example.bghelp.ui.screens.createalarm.CreateAlarmScreen
import com.example.bghelp.ui.screens.settings.SettingsScreen
import com.example.bghelp.ui.screens.task.add.AddTaskScreen
import com.example.bghelp.ui.screens.task.main.TaskScreen
import com.example.bghelp.ui.screens.task.TaskWallpaperScreen
import com.example.bghelp.ui.screens.locationpicker.LocationPickerScreen
import com.example.bghelp.ui.screens.target.AddTargetScreen
import com.example.bghelp.ui.screens.target.TargetScreen
import com.example.bghelp.ui.screens.target.TargetWallpaperScreen
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.home.HomeScreen
import com.example.bghelp.ui.screens.home.HomeWallpaperScreen
import com.example.bghelp.ui.screens.items.AddItemScreen
import com.example.bghelp.ui.screens.items.AddItemViewModel
import com.example.bghelp.ui.screens.target.AddTargetViewModel
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.colorpicker.ColorPickerScreen

private const val OVERLAY_GRAPH_ROUTE = "overlay_graph"

private inline fun NavGraphBuilder.noTransitionComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    crossinline content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) { backStackEntry ->
        content(backStackEntry)
    }
}

@Composable
fun BottomNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Tasks.Main.route,
        modifier = modifier
    ) {
        noTransitionComposable(route = Screen.Home.Main.route) { _ ->
            HomeScreen()
        }
        noTransitionComposable(route = Screen.Tasks.Main.route) { _ ->
            TaskScreen()
        }
        noTransitionComposable(route = Screen.Targets.Main.route) { _ ->
            TargetScreen()
        }
        noTransitionComposable(route = Screen.Items.Main.route) { _ ->
            ItemScreen()
        }
        noTransitionComposable(route = Screen.Notes.Main.route) { _ ->
            NoteScreen()
        }
    }
}

@Composable
fun OverlayNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "empty",
        modifier = modifier,
        route = OVERLAY_GRAPH_ROUTE
    ) {
        noTransitionComposable(route = "empty") { _ ->
            /* Empty placeholder */
        }

        // Add screens (full screens)
        noTransitionComposable(route = Screen.Tasks.Add.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(OVERLAY_GRAPH_ROUTE)
            }
            val viewModel: AddTaskViewModel = hiltViewModel(parentEntry)
            AddTaskScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        noTransitionComposable(route = Screen.Targets.Add.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(OVERLAY_GRAPH_ROUTE)
            }
            val viewModel: AddTargetViewModel = hiltViewModel(parentEntry)
            AddTargetScreen(
                viewModel = viewModel,
                onTargetCreated = { navController.popBackStack() }
            )
        }

        noTransitionComposable(route = Screen.Items.Add.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(OVERLAY_GRAPH_ROUTE)
            }
            val viewModel: AddItemViewModel = hiltViewModel(parentEntry)
            AddItemScreen(
                viewModel = viewModel,
                onItemCreated = { navController.popBackStack() }
            )
        }

        noTransitionComposable(route = Screen.Notes.Add.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(OVERLAY_GRAPH_ROUTE)
            }
            val viewModel: AddNoteViewModel = hiltViewModel(parentEntry)
            AddNoteModal(
                viewModel = viewModel,
                onNoteCreated = { navController.popBackStack() }
            )
        }

        // Options screens
        noTransitionComposable(route = Screen.Options.Settings.route) { _ ->
            SettingsScreen(
                onNavigateToCreateAlarm = {
                    navController.navigate(Screen.Options.CreateAlarm.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        noTransitionComposable(route = Screen.Options.CreateAlarm.route) { _ ->
            CreateAlarmScreen()
        }

        noTransitionComposable(route = Screen.Options.ColorPicker.route) { _ ->
            ColorPickerScreen(navController = navController)
        }

        noTransitionComposable(
            route = Screen.LocationPicker.ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(Screen.LocationPicker.ALLOW_MULTIPLE_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val allowMultiple = backStackEntry.arguments?.getBoolean(
                Screen.LocationPicker.ALLOW_MULTIPLE_ARG
            ) ?: false
            LocationPickerScreen(
                navController = navController,
                allowMultiple = allowMultiple
            )
        }

        // Wallpaper screens for each feature
        noTransitionComposable(route = Screen.Home.Wallpaper.route) { _ ->
            HomeWallpaperScreen()
        }
        noTransitionComposable(route = Screen.Tasks.Wallpaper.route) { _ ->
            TaskWallpaperScreen()
        }
        noTransitionComposable(route = Screen.Targets.Wallpaper.route) { _ ->
            TargetWallpaperScreen()
        }
        noTransitionComposable(route = Screen.Items.Wallpaper.route) { _ ->
            ItemWallpaperScreen()
        }
        noTransitionComposable(route = Screen.Notes.Wallpaper.route) { _ ->
            NoteWallpaperScreen()
        }
    }
}

@Composable
fun MainNavHost(
    bottomNavController: NavHostController,
    overlayNavController: NavHostController,
    isOptionsActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Bottom navigation content (base layer)
        if (!isOptionsActive) {
            BottomNavHost(
                navController = bottomNavController,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Options/Modal screens (overlay layer)
        OverlayNavHost(
            navController = overlayNavController,
            modifier = Modifier.fillMaxSize()
        )
    }
}