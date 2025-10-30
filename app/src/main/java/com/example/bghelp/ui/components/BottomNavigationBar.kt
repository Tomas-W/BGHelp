package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bghelp.R
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.theme.BottomNavSelected
import com.example.bghelp.ui.theme.BottomNavUnselected
import com.example.bghelp.ui.theme.Sizes

@Composable
private fun getIconResource(screen: Screen, currentRoute: String?): Int {
    val isSelected = currentRoute == screen.route
    return remember(screen, isSelected) {
        when (screen) {
            Screen.Tasks.Main -> if (isSelected) R.drawable.tasks_filled else R.drawable.tasks_outlined
            Screen.Targets.Main -> if (isSelected) R.drawable.targets_filled else R.drawable.targets_outlined
            Screen.Items.Main -> if (isSelected) R.drawable.items_filled else R.drawable.items_outlined
            Screen.Events.Main -> if (isSelected) R.drawable.events_filled else R.drawable.events_outlined
            else -> R.drawable.tasks_outlined
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    screen: Screen,
    currentRoute: String?,
    onClick: () -> Unit
) {
    val isSelected = currentRoute == screen.route
    val iconRes = getIconResource(screen, currentRoute)
    
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { 
            Image(
                painter = painterResource(iconRes),
                contentDescription = screen.title,
                modifier = Modifier.size(Sizes.Icon.Large),
                colorFilter = ColorFilter.tint(
                    if (isSelected) MaterialTheme.colorScheme.onSurface
                     else MaterialTheme.colorScheme.onTertiary
                )
            )
        },
        label = { 
            Text(
                text = screen.title,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onTertiary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        colors = NavigationBarItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onBackground,
            selectedTextColor = MaterialTheme.colorScheme.onBackground,
            unselectedIconColor = MaterialTheme.colorScheme.onTertiary,
            unselectedTextColor = MaterialTheme.colorScheme.onTertiary,
            selectedIndicatorColor = MaterialTheme.colorScheme.secondary,
            disabledIconColor = MaterialTheme.colorScheme.onTertiary,
            disabledTextColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    isOptionsActive: Boolean = false,
    onRequestNavigateTo: ((String) -> Unit)? = null,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = remember(isOptionsActive, navBackStackEntry?.destination?.route) {
        if (isOptionsActive) null else navBackStackEntry?.destination?.route
    }

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

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalHeight = remember(navigationBarHeight) { navigationBarHeight + UI.BOTTOM_BAR_HEIGHT.dp }
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight),
        windowInsets = WindowInsets(bottom = navigationBarHeight, top = 6.dp),
    ) {
        // Home
        NavigationItem(
            screen = Screen.Home.Main,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Home.Main.route) }
        )

        // Tasks
        NavigationItem(
            screen = Screen.Tasks.Main,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Tasks.Main.route) }
        )

        // Targets
        NavigationItem(
            screen = Screen.Targets.Main,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Targets.Main.route) }
        )

         // Items
        NavigationItem(
            screen = Screen.Items.Main,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Items.Main.route) }
        )

        // Events
        NavigationItem(
            screen = Screen.Events.Main,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Events.Main.route) }
        )
    }
}
