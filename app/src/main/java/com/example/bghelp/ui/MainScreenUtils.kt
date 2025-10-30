package com.example.bghelp.ui

import androidx.navigation.NavController
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

fun navigateToWallpaperScreen(navController: NavController, currentScreen: Screen) {
    val wallpaperRoute = when (currentScreen) {
        is Screen.Home -> Screen.Home.Wallpaper.route
        is Screen.Tasks -> Screen.Tasks.Wallpaper.route
        is Screen.Targets -> Screen.Targets.Wallpaper.route
        is Screen.Items -> Screen.Items.Wallpaper.route
        is Screen.Events -> Screen.Events.Wallpaper.route
        else -> Screen.Home.Wallpaper.route
    }
    navController.navigate(wallpaperRoute) {
        launchSingleTop = true
    }
}

fun navigateToAddScreen(navController: NavController, currentScreen: Screen) {
    val addRoute = when (currentScreen) {
        is Screen.Tasks -> Screen.Tasks.Add.route
        is Screen.Targets -> Screen.Targets.Add.route
        is Screen.Items -> Screen.Items.Add.route
        is Screen.Events -> Screen.Events.Add.route
        else -> null
    }
    if (addRoute != null) {
        navController.navigate(addRoute) {
            launchSingleTop = true
        }
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
    overlayNavController.navigate("empty") {
        popUpTo("empty") { inclusive = true }
        launchSingleTop = true
    }
    bottomNavController.navigate(route) {
        popUpTo(bottomNavController.graph.startDestinationId) { inclusive = false }
        launchSingleTop = true
    }
}
