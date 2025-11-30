package com.example.bghelp.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.bghelp.ui.navigation.Screen

fun getCurrentScreen(bottomRoute: String?, optionsRoute: String?): Screen {
    val effectiveRoute = if (optionsRoute != null && optionsRoute != "empty") {
        optionsRoute
    } else {
        bottomRoute
    }
    return Screen.getScreenByRoute(effectiveRoute)
}

fun isOptionsScreenActive(route: String?): Boolean {
    return route != null && route != "empty"
}

fun navigateToOptionsScreen(navController: NavController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
    }
}

fun handleBackNavigation(navController: NavController, currentScreen: Screen) {
    if (currentScreen in Screen.optionsScreens) {
        popAllOptionsScreens(navController)
    } else {
        navController.popBackStack()
    }
}

fun popAllOptionsScreens(navController: NavController) {
    val optionsRoutes = Screen.optionsScreens.map { it.route }
    var popped = navController.popBackStack()
    while (popped && navController.currentBackStackEntry?.destination?.route in optionsRoutes) {
        popped = navController.popBackStack()
    }
}

fun resetAndNavigateToTab(
    overlayNavController: NavController,
    bottomNavController: NavController,
    route: String
) {
    val overlayDestination = overlayNavController.currentDestination?.route
    if (overlayDestination != null && overlayDestination != "empty") {
        val didPop = overlayNavController.popBackStack("empty", inclusive = false)
        if (!didPop) {
            overlayNavController.navigate("empty") {
                popUpTo("empty") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val startDestinationId = bottomNavController.graph.findStartDestination().id
    if (bottomNavController.currentDestination?.route != route) {
        bottomNavController.navigate(route) {
            popUpTo(startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
