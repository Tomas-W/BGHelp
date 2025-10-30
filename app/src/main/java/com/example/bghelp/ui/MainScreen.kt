package com.example.bghelp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.components.BottomNavigationBar
import com.example.bghelp.ui.components.TopBar
import com.example.bghelp.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val optionsBackStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = getCurrentScreen(
        bottomRoute = navBackStackEntry?.destination?.route,
        optionsRoute = optionsBackStackEntry?.destination?.route
    )
    val isOptionsActive = isOptionsScreenActive(optionsBackStackEntry?.destination?.route)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerScreen(
                currentRoute = optionsBackStackEntry?.destination?.route,
                onNavigateToScreen = { route ->
                    navigateToOptionsScreen(navController, route)
                },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    title = currentScreen.title,
                    showWallpaperIcon = currentScreen in Screen.bottomNavScreens,
                    onScreenshotClick = {
                        navigateToWallpaperScreen(navController, currentScreen)
                    },
                    onBackClick = {
                        handleBackNavigation(navController, currentScreen)
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                if (currentScreen in Screen.featureMains) {
                    FloatingActionButton(
                        onClick = { navigateToAddScreen(navController, currentScreen) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = bottomNavController,
                    isOptionsActive = isOptionsActive,
                    onRequestNavigateTo = { route ->
                        resetAndNavigateToTab(navController, bottomNavController, route)
                    }
                )
            }
        ) { innerPadding ->
            MainNavHost(
                bottomNavController = bottomNavController,
                overlayNavController = navController,
                isOptionsActive = isOptionsActive,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            )
        }
    }
}
