package com.example.bghelp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bghelp.utils.Screen

/**
 * Bottom navigation bar with 5 main tabs
 * Handles navigation between Tasks, Targets, Items, Events, and Settings
 */
@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    isOptionsActive: Boolean = false,
    onRequestNavigateTo: ((String) -> Unit)? = null,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = if (isOptionsActive) null else navBackStackEntry?.destination?.route

    fun navigateTo(route: String) {
        if (isOptionsActive && onRequestNavigateTo != null) {
            onRequestNavigateTo.invoke(route)
            return
        }
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute == Screen.Tasks.Home.route,
            onClick = { navigateTo(Screen.Tasks.Home.route) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Tasks") },
            label = { Text(Screen.Tasks.Home.title) }
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Targets.Home.route,
            onClick = { navigateTo(Screen.Targets.Home.route) },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Targets") },
            label = { Text(Screen.Targets.Home.title) }
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Items.Home.route,
            onClick = { navigateTo(Screen.Items.Home.route) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Items") },
            label = { Text(Screen.Items.Home.title) }
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Events.Home.route,
            onClick = { navigateTo(Screen.Events.Home.route) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Events") },
            label = { Text(Screen.Events.Home.title) }
        )
    }
}
