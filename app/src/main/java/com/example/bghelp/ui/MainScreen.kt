package com.example.bghelp.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.navigation.Screen.Companion.weekNavigationScreens
import com.example.bghelp.ui.theme.MainBlue
import com.example.bghelp.ui.theme.Sizes

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
    // WeekNav State
    val isTaskScreen = currentScreen in weekNavigationScreens
    var weekNavIsExpanded by remember { mutableStateOf(false) }
    var weekNavHeight by remember { mutableStateOf(0.dp) }
    // WeekNav callback
    val onWeekNavExpansionChanged: (Boolean, Dp) -> Unit = { isExpanded, height ->
        weekNavIsExpanded = isExpanded
        weekNavHeight = height
    }
    // FAB state
    val fabOffset by animateDpAsState( // Depends on weekNav presence
        targetValue = if (isTaskScreen && weekNavIsExpanded) {
            weekNavHeight + Sizes.Icon.L // Extra offset for WeekNav border visibility changes
        } else {
            0.dp
        },
        animationSpec = tween(300),
        label = "FAB Offset"
    )
    // Options drawer
    var showOptionsMenu by remember { mutableStateOf(false) }
    val isOptionsActive = isOptionsScreenActive(optionsBackStackEntry?.destination?.route)
    // Bottom nav
    val shouldHideBottomNav = currentScreen in Screen.noBottomNavScreens

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                // Hide FAB when drawer is shown
                if (currentScreen in Screen.featureMains && !showOptionsMenu) {
                    FloatingActionButton(
                        onClick = { navigateToAddScreen(navController, currentScreen) },
                        containerColor = MainBlue,
                        modifier = Modifier.offset(y = -fabOffset)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            },
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
                        isDrawerShown = showOptionsMenu
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                MainNavHost(
                    bottomNavController = bottomNavController,
                    overlayNavController = navController,
                    isOptionsActive = isOptionsActive,
                    onWeekNavExpansionChanged = onWeekNavExpansionChanged,
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
    }
}
