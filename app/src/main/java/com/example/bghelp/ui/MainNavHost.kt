package com.example.bghelp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bghelp.ui.screens.events.AddEventModal
import com.example.bghelp.ui.screens.events.EventScreen
import com.example.bghelp.ui.screens.events.EventWallpaperScreen
import com.example.bghelp.ui.screens.items.ItemScreen
import com.example.bghelp.ui.screens.items.ItemWallpaperScreen
import com.example.bghelp.ui.screens.options.CreateAlarmScreen
import com.example.bghelp.ui.screens.options.SettingsScreen
import com.example.bghelp.ui.screens.task.AddTaskScreen
import com.example.bghelp.ui.screens.task.TaskScreen
import com.example.bghelp.ui.screens.task.TaskWallpaperScreen
import com.example.bghelp.ui.screens.target.AddTargetScreen
import com.example.bghelp.ui.screens.target.TargetScreen
import com.example.bghelp.ui.screens.target.TargetWallpaperScreen
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.home.HomeScreen
import com.example.bghelp.ui.screens.home.HomeWallpaperScreen
import com.example.bghelp.ui.screens.items.AddItemScreen

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
        composable(Screen.Home.Main.route) { HomeScreen() }
        composable(Screen.Tasks.Main.route) { TaskScreen() }
        composable(Screen.Targets.Main.route) { TargetScreen() }
        composable(Screen.Items.Main.route) {
            ItemScreen(viewModel = hiltViewModel()) 
        }
        composable(Screen.Events.Main.route) { EventScreen() }
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
        modifier = modifier
    ) {
        composable("empty") { /* Empty placeholder */ }

        // Add screens (full screens)
        composable(Screen.Tasks.Add.route) {
            AddTaskScreen()
        }

        composable(Screen.Targets.Add.route) {
            AddTargetScreen(onTargetCreated = { navController.popBackStack() })
        }

        // Modal screens
        composable(Screen.Items.Add.route) {
            AddItemScreen(onItemCreated = { navController.popBackStack() })
        }

        composable(Screen.Events.Add.route) {
            AddEventModal(onEventCreated = { navController.popBackStack() })
        }

        // Options screens
        composable(Screen.Options.Settings.route) {
            SettingsScreen(
                onNavigateToCreateAlarm = {
                    navController.navigate(Screen.Options.CreateAlarm.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Options.CreateAlarm.route) {
            CreateAlarmScreen()
        }

        // Wallpaper screens for each feature
        composable(Screen.Home.Wallpaper.route) { HomeWallpaperScreen() }
        composable(Screen.Tasks.Wallpaper.route) { TaskWallpaperScreen() }
        composable(Screen.Targets.Wallpaper.route) { TargetWallpaperScreen() }
        composable(Screen.Items.Wallpaper.route) { ItemWallpaperScreen() }
        composable(Screen.Events.Wallpaper.route) { EventWallpaperScreen() }
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