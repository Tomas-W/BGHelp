package com.example.bghelp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.components.OptionsModal
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.screens.alert.AlertTrackingViewModel
import com.example.bghelp.ui.screens.task.main.TaskNavigationKeys
import com.example.bghelp.ui.screens.task.main.TaskPreviewComponent
import java.time.ZoneId

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val optionsBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = getCurrentScreen(
        bottomRoute = navBackStackEntry?.destination?.route,
        optionsRoute = optionsBackStackEntry?.destination?.route
    )
    // Options drawer
    var showOptionsMenu by remember { mutableStateOf(false) }
    val isOptionsActive = isOptionsScreenActive(optionsBackStackEntry?.destination?.route)
    // Bottom nav
    val shouldHideBottomNav = currentScreen in Screen.noBottomNavScreens
    
    // Alert tracking
    val alertViewModel: AlertTrackingViewModel = hiltViewModel()
    val triggeredAlert by alertViewModel.triggeredAlert.collectAsState()
    val triggeredTask by alertViewModel.triggeredTask.collectAsState()
    
    // Track when we need to navigate to a task
    var taskToNavigateTo by remember { mutableStateOf<Pair<Int, Long>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (!shouldHideBottomNav) {
                    BottomNavigationBar(
                        navController = bottomNavController,
                        isOptionsActive = isOptionsActive,
                        overlayRoute = optionsBackStackEntry?.destination?.route,
                        onRequestNavigateTo = { route ->
                            resetAndNavigateToTab(navController, bottomNavController, route)
                        },
                        onOptionsClick = {
                            showOptionsMenu = !showOptionsMenu
                        },
                        isDrawerShown = showOptionsMenu,
                        onDrawerDismiss = {
                            showOptionsMenu = false
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavHost(
                    bottomNavController = bottomNavController,
                    overlayNavController = navController,
                    isOptionsActive = isOptionsActive,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )

                // No drawer needed when no BottomBar to open it
                if (!shouldHideBottomNav) {
                    OptionsDrawer(
                        visible = showOptionsMenu,
                        currentRoute = optionsBackStackEntry?.destination?.route,
                        onNavigateToScreen = { route ->
                            navigateToOptionsScreen(navController, route)
                            showOptionsMenu = false
                        },
                        onDismiss = {
                            showOptionsMenu = false
                        }
                    )
                }
            }
        }
        
        // Handle navigation to task after alert
        LaunchedEffect(taskToNavigateTo) {
            taskToNavigateTo?.let { (taskId, taskDateMillis) ->
                // Navigate to Tasks.Main if not already there
                if (navBackStackEntry?.destination?.route != Screen.Tasks.Main.route) {
                    resetAndNavigateToTab(navController, bottomNavController, Screen.Tasks.Main.route)
                    delay(200)
                }
                
                // Set the values on overlay nav controller - TaskScreen will observe and react
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    TaskNavigationKeys.TASK_DATE_TO_NAVIGATE_TO,
                    taskDateMillis
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    TaskNavigationKeys.TASK_ID_TO_SCROLL_TO,
                    taskId
                )
                
                taskToNavigateTo = null
            }
        }
        
        // Alert Modal
        OptionsModal(
            isVisible = triggeredAlert != null && triggeredTask != null,
            onDismissRequest = { alertViewModel.dismissAlert() },
            title = if (triggeredAlert?.reminder != null) "Reminder" else "Task Starting",
            content = {
                triggeredTask?.let { task ->
                    TaskPreviewComponent(task = task)
                }
            },
            secondLabel = "Dismiss",
            secondOnClick = { alertViewModel.dismissAlert() },
            firstLabel = "Go to Task",
            firstOnClick = {
                triggeredTask?.let { task ->
                    val taskDateMillis = task.date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    taskToNavigateTo = task.id to taskDateMillis
                    alertViewModel.dismissAlert()
                }
            }
        )
    }
}
