package com.example.bghelp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.navigation.Screen

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
    }
}
