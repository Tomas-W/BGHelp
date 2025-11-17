package com.example.bghelp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.theme.MainBlue

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavController = rememberNavController()
    var showOptionsMenu by remember { mutableStateOf(false) }

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val optionsBackStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = getCurrentScreen(
        bottomRoute = navBackStackEntry?.destination?.route,
        optionsRoute = optionsBackStackEntry?.destination?.route
    )
    val isOptionsActive = isOptionsScreenActive(optionsBackStackEntry?.destination?.route)
    val shouldHideBottomNav = currentScreen in Screen.noBottomNavScreens

    Scaffold(
        floatingActionButton = {
            if (currentScreen in Screen.featureMains) {
                FloatingActionButton(
                    onClick = { navigateToAddScreen(navController, currentScreen) },
                    containerColor = MainBlue
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
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            )
            
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
