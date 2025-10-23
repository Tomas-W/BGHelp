package com.example.bghelp.ui

import SettingsScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.components.TopBar
import com.example.bghelp.ui.screens.task.TaskScreen
import com.example.bghelp.ui.screens.target.TargetScreen
import com.example.bghelp.ui.screens.items.ItemScreen
import com.example.bghelp.ui.screens.events.EventScreen
import com.example.bghelp.ui.screens.task.TaskWallpaperScreen
import com.example.bghelp.ui.screens.target.TargetWallpaperScreen
import com.example.bghelp.ui.screens.items.ItemWallpaperScreen
import com.example.bghelp.ui.screens.events.EventWallpaperScreen
import com.example.bghelp.ui.screens.task.AddTaskScreen
import com.example.bghelp.ui.screens.target.AddTargetScreen
import com.example.bghelp.ui.screens.items.AddItemModal
import com.example.bghelp.ui.screens.events.AddEventModal
import com.example.bghelp.ui.screens.options.CreateAlarmScreen
import com.example.bghelp.utils.Screen

/**
 * Simplified MainScreen with working navigation
 * This replaces the complex navigation structure with a simpler working version
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Get current route to determine title using type-safe Screen class
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val optionsBackStackEntry by navController.currentBackStackEntryAsState()
    val currentOptionsRoute = optionsBackStackEntry?.destination?.route
    val effectiveRoute = if (currentOptionsRoute != null && currentOptionsRoute != "empty") currentOptionsRoute else currentRoute
    val currentScreen = Screen.getScreenByRoute(effectiveRoute)
    val title = currentScreen.title
    val isOptionsActive = currentOptionsRoute != null && currentOptionsRoute != "empty"
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.systemBarsPadding().width(260.dp)) {
                DrawerContent(
                    currentRoute = currentOptionsRoute,
                    onNavigateSettings = {
                        navController.navigate(Screen.Options.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateCreateAlarm = {
                        navController.navigate(Screen.Options.CreateAlarm.route) {
                            launchSingleTop = true
                        }
                    },
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    title = title,
                    showWallpaperIcon = currentScreen in Screen.bottomNavScreens,
                    onScreenshotClick = {
                        // Navigate to wallpaper screen based on current feature
                        when (currentScreen) {
                            is Screen.Tasks -> navController.navigate(Screen.Tasks.Wallpaper.route) {
                                launchSingleTop = true
                            }
                            is Screen.Targets -> navController.navigate(Screen.Targets.Wallpaper.route) {
                                launchSingleTop = true
                            }
                            is Screen.Items -> navController.navigate(Screen.Items.Wallpaper.route) {
                                launchSingleTop = true
                            }
                            is Screen.Events -> navController.navigate(Screen.Events.Wallpaper.route) {
                                launchSingleTop = true
                            }
                            else -> navController.navigate(Screen.Tasks.Wallpaper.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onBackClick = {
                        // If currently in an Options screen, pop all Options screens
                        if (currentScreen in Screen.optionsScreens) {
                            // Pop back repeatedly until we're out of Options screens
                            var popped = navController.popBackStack()
                            while (popped && navController.currentBackStackEntry?.destination?.route in Screen.optionsScreens.map { it.route }) {
                                popped = navController.popBackStack()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = bottomNavController,
                    isOptionsActive = isOptionsActive,
                    onRequestNavigateTo = { route ->
                        // Close overlay and navigate tab - reset both stacks
                        navController.navigate("empty") {
                            popUpTo("empty") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        bottomNavController.navigate(route) {
                            popUpTo(bottomNavController.graph.startDestinationId) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Bottom navigation content (base layer)
            if (!isOptionsActive) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = Screen.Tasks.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Tasks.Home.route) { TaskScreen() }
                    composable(Screen.Targets.Home.route) { TargetScreen() }
                    composable(Screen.Items.Home.route) { ItemScreen() }
                    composable(Screen.Events.Home.route) { EventScreen() }
                }
            }
            
            // Options/Modal screens (overlay layer)
            NavHost(
                navController = navController,
                startDestination = "empty",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("empty") { /* Empty placeholder */ }
                
                // Add screens (full screens)
                composable(Screen.Tasks.Add.route) {
                    AddTaskScreen(
                        onTaskCreated = { navController.popBackStack() }
                    )
                }
                
                composable(Screen.Targets.Add.route) {
                    AddTargetScreen(
                        onTargetCreated = { navController.popBackStack() }
                    )
                }
                
                // Modal screens
                composable(Screen.Items.Add.route) {
                    AddItemModal(
                        onItemCreated = { navController.popBackStack() }
                    )
                }
                
                composable(Screen.Events.Add.route) {
                    AddEventModal(
                        onEventCreated = { navController.popBackStack() }
                    )
                }
                
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
                    CreateAlarmScreen(
                        onAlarmCreated = { 
                            navController.navigate(Screen.Options.Settings.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                
                // Wallpaper screens for each feature
                composable(Screen.Tasks.Wallpaper.route) {
                    TaskWallpaperScreen()
                }
                
                composable(Screen.Targets.Wallpaper.route) {
                    TargetWallpaperScreen()
                }
                
                composable(Screen.Items.Wallpaper.route) {
                    ItemWallpaperScreen()
                }
                
                composable(Screen.Events.Wallpaper.route) {
                    EventWallpaperScreen()
                }
            }
        }
        }
    }
}

@Composable
private fun DrawerContent(
    currentRoute: String?,
    onNavigateSettings: () -> Unit,
    onNavigateCreateAlarm: () -> Unit,
    closeDrawer: () -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        DrawerItem(
            icon = Icons.Default.Settings,
            label = Screen.Options.Settings.title,
            selected = currentRoute == Screen.Options.Settings.route,
            onClick = {
                onNavigateSettings()
                closeDrawer()
            }
        )
        DrawerItem(
            icon = Icons.Default.Notifications,
            label = Screen.Options.CreateAlarm.title,
            selected = currentRoute == Screen.Options.CreateAlarm.route,
            onClick = {
                onNavigateCreateAlarm()
                closeDrawer()
            }
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = NavigationDrawerItemDefaults.colors()
    NavigationDrawerItem(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        icon = { Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
        colors = colors,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}