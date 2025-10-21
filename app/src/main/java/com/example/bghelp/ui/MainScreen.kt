package com.example.bghelp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bghelp.ui.screens.target.TargetScreen
import com.example.bghelp.ui.screens.task.TaskScreen
import com.example.bghelp.utils.Screen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Tasks
            NavigationDrawerItem(
                selected = currentRoute == Screen.TaskScreen.route,
                onClick = {
                    if (currentRoute != Screen.TaskScreen.route) {
                        navController.navigate(Screen.TaskScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(Icons.Default.DateRange, contentDescription = "Task overview") },
                label = { Text("Task overview") }
            )
            // Targets
            NavigationDrawerItem(
                selected = currentRoute == Screen.TargetScreen.route,
                onClick = {
                    if (currentRoute != Screen.TargetScreen.route) {
                        navController.navigate(Screen.TargetScreen.route)
                    }
                    scope.launch { drawerState.close() }
                },
                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Target overview") },
                label = { Text("Target overview") }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Open drawer") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Open drawer")
                        }
                    },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    // Task
                    NavigationBarItem(
                        selected = currentRoute == Screen.TaskScreen.route,
                        onClick = {
                            if (currentRoute != Screen.TaskScreen.route) {
                                navController.navigate(Screen.TaskScreen.route)
                            }
                        },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Task overview") },
                        label = { Text("Task overview") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.TargetScreen.route,
                        onClick = {
                            if (currentRoute != Screen.TargetScreen.route) {
                                navController.navigate(Screen.TargetScreen.route)
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Target overview") },
                        label = { Text("Target overview") }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.TaskScreen.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.TaskScreen.route) { TaskScreen() }
                composable(Screen.TargetScreen.route) { TargetScreen() }
            }
        }
    }
}