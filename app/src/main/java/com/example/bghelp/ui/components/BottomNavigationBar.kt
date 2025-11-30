package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.bghelp.R
import com.example.bghelp.ui.navigation.Screen
import com.example.bghelp.ui.navigation.getLocalizedTitle
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.topBorder
import com.example.bghelp.constants.UiConstants as UI

@Composable
private fun getIconResource(screen: Screen, currentRoute: String?): Int {
    val isSelected = currentRoute == screen.route
    return remember(screen, isSelected) {
        when (screen) {
            Screen.Tasks.Main -> if (isSelected) R.drawable.tasks_filled else R.drawable.tasks_outlined
            Screen.Targets.Main -> if (isSelected) R.drawable.targets_filled else R.drawable.targets_outlined
            Screen.Items.Main -> if (isSelected) R.drawable.items_filled else R.drawable.items_outlined
            Screen.Notes.Main -> if (isSelected) R.drawable.notes_filled else R.drawable.notes_outlined
            else -> R.drawable.tasks_outlined
        }
    }
}

@Composable
private fun getOptionsIconResource(isSelected: Boolean): Int {
    return remember(isSelected) {
        if (isSelected) R.drawable.options_active else R.drawable.options_inactive
    }
}

@Composable
private fun RowScope.NavigationItem(
    screen: Screen,
    currentRoute: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconRes = getIconResource(screen, currentRoute)

    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Image(
                painter = painterResource(iconRes),
                contentDescription = screen.getLocalizedTitle(),
                modifier = Modifier.size(Sizes.Icon.L),
                colorFilter = ColorFilter.tint(
                    if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onTertiary
                )
            )
        },
        label = {
            Text(
                text = screen.getLocalizedTitle(),
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onTertiary,
                fontWeight = if (isSelected) FontWeight.Bold
                             else FontWeight.Normal,
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
    overlayRoute: String? = null,
    onRequestNavigateTo: ((String) -> Unit)? = null,
    onOptionsClick: (() -> Unit)? = null,
    isDrawerShown: Boolean = false,
    onDrawerDismiss: (() -> Unit)? = null,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Track the last known bottom route for highlighting
    val bottomRoute = navBackStackEntry?.destination?.route
    var lastKnownBottomRoute by rememberSaveable {
        mutableStateOf(bottomRoute)
    }
    LaunchedEffect(bottomRoute) {
        if (bottomRoute != null) {
            lastKnownBottomRoute = bottomRoute
        }
    }
    val normalizedOverlayRoute = overlayRoute?.takeUnless { it == "empty" }
    val isOptionsRoute = normalizedOverlayRoute != null &&
            normalizedOverlayRoute in Screen.optionsScreens.map { it.route }

    val currentRoute = when {
        isDrawerShown -> null
        isOptionsRoute -> null
        isOptionsActive -> lastKnownBottomRoute
        else -> bottomRoute
    }

    fun navigateTo(route: String) {
        if (isDrawerShown) {
            onDrawerDismiss?.invoke()
        }
        if (isOptionsActive && onRequestNavigateTo != null) {
            onRequestNavigateTo.invoke(route)
            return
        }
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalHeight =
        remember(navigationBarHeight) { navigationBarHeight + UI.BOTTOM_BAR_HEIGHT.dp }
    val normalizedOverlayRouteForOptions = overlayRoute?.takeUnless { it == "empty" }
    val isOptionsRouteActive = isDrawerShown || (normalizedOverlayRouteForOptions != null &&
            normalizedOverlayRouteForOptions in Screen.optionsScreens.map { it.route })

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .topBorder(),
        containerColor = MaterialTheme.colorScheme.background,
        windowInsets = WindowInsets(
            bottom = navigationBarHeight,
            top = 12.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            // Tasks
            NavigationItem(
                screen = Screen.Tasks.Main,
                currentRoute = currentRoute,
                isSelected = currentRoute in Screen.taskScreens,
                onClick = { navigateTo(Screen.Tasks.Main.route) }
            )

            // Targets
            NavigationItem(
                screen = Screen.Targets.Main,
                currentRoute = currentRoute,
                isSelected = currentRoute in Screen.targetScreens,
                onClick = { navigateTo(Screen.Targets.Main.route) }
            )

            // Items
            NavigationItem(
                screen = Screen.Items.Main,
                currentRoute = currentRoute,
                isSelected = currentRoute in Screen.itemScreens,
                onClick = { navigateTo(Screen.Items.Main.route) }
            )

            // Notes
            NavigationItem(
                screen = Screen.Notes.Main,
                currentRoute = currentRoute,
                isSelected = currentRoute in Screen.noteScreens,
                onClick = { navigateTo(Screen.Notes.Main.route) }
            )

            // Options
            val optionsIconRes = getOptionsIconResource(isOptionsRouteActive)
            NavigationBarItem(
                selected = isOptionsRouteActive,
                onClick = { onOptionsClick?.invoke() ?: Unit },
                icon = {
                    Image(
                        painter = painterResource(optionsIconRes),
                        contentDescription = stringResource(R.string.extra_options),
                        modifier = Modifier.size(Sizes.Icon.L),
                        colorFilter = ColorFilter.tint(
                            if (isOptionsRouteActive) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onTertiary
                        )
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.extra_options),
                        color = if (isOptionsRouteActive) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onTertiary,
                        fontWeight = if (isOptionsRouteActive) FontWeight.Bold
                                     else FontWeight.Normal,
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
    }
}
