package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bghelp.R
import com.example.bghelp.utils.Screen
import com.example.bghelp.ui.theme.BottomNavSelected
import com.example.bghelp.ui.theme.BottomNavUnselected
import com.example.bghelp.ui.theme.SecondaryBlue
import com.example.bghelp.ui.theme.Sizes


@Composable
private fun RowScope.NavigationItem(
    screen: Screen,
    currentRoute: String?,
    onClick: () -> Unit
) {
    val iconRes = when (screen) {
        Screen.Tasks.Home -> if (currentRoute == screen.route) R.drawable.tasks_filled else R.drawable.tasks_outlined
        Screen.Targets.Home -> if (currentRoute == screen.route) R.drawable.targets_filled else R.drawable.targets_outlined
        Screen.Items.Home -> if (currentRoute == screen.route) R.drawable.items_filled else R.drawable.items_outlined
        Screen.Events.Home -> if (currentRoute == screen.route) R.drawable.events_filled else R.drawable.events_outlined
        else -> R.drawable.tasks_outlined
    }
    
    NavigationBarItem(
        selected = currentRoute == screen.route,
        onClick = onClick,
        icon = { 
            Image(
                painter = painterResource(iconRes),
                contentDescription = screen.title,
                modifier = Modifier.size(Sizes.Icon.Large),
                colorFilter = ColorFilter.tint(
                    if (currentRoute == screen.route) BottomNavSelected else BottomNavUnselected
                )
            )
        },
        label = { 
            Text(
                text = screen.title,
                color = if (currentRoute == screen.route) BottomNavSelected else BottomNavUnselected,
                fontWeight = if (currentRoute == screen.route) FontWeight.Bold else FontWeight.Normal,
            )
        },
        colors = NavigationBarItemColors(
            selectedIconColor = BottomNavSelected,
            selectedTextColor = BottomNavSelected,
            unselectedIconColor = BottomNavUnselected,
            unselectedTextColor = BottomNavUnselected,
            selectedIndicatorColor = SecondaryBlue,
            disabledIconColor = BottomNavUnselected,
            disabledTextColor = BottomNavUnselected
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
    val padding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totHeight = padding + 64.dp
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(totHeight)
            .padding(bottom = padding),
        windowInsets = WindowInsets(0, 12)
    ) {
        NavigationItem(
            screen = Screen.Events.Home,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Events.Home.route) }
        )

        NavigationItem(
            screen = Screen.Tasks.Home,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Tasks.Home.route) }
        )
        
        NavigationItem(
            screen = Screen.Targets.Home,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Targets.Home.route) }
        )
        
        NavigationItem(
            screen = Screen.Items.Home,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Items.Home.route) }
        )
        
        NavigationItem(
            screen = Screen.Events.Home,
            currentRoute = currentRoute,
            onClick = { navigateTo(Screen.Events.Home.route) }
        )
    }
}