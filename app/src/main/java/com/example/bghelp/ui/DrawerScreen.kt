package com.example.bghelp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.navigation.Screen

@Composable
fun OptionsDrawer(
    visible: Boolean,
    currentRoute: String?,
    onNavigateToScreen: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarHeight = UI.BOTTOM_BAR_HEIGHT.dp
    val bottomOffset = navigationBarHeight + bottomBarHeight
    val bottomOffsetPx = with(density) { bottomOffset.toPx().toInt() }
    
    // Scrim to mute all but BottomNav and drawer
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomOffset)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .clickable(onClick = onDismiss)
        )
    }
    
    // Drawer slides up from underneath BottomBar
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(320),
                initialOffsetY = { fullHeight -> fullHeight + bottomOffsetPx } // Start hidden
            ) + fadeIn(animationSpec = tween(200)),
            exit = slideOutVertically(
                animationSpec = tween(280),
                targetOffsetY = { fullHeight -> fullHeight + bottomOffsetPx } // Slide its height
            ) + fadeOut(animationSpec = tween(200)),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomStart)
                .padding(bottom = bottomOffset) // Above BottomBar
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(enabled = false) { }
            ) {
                DrawerContent(
                    currentRoute = currentRoute,
                    onNavigateToScreen = onNavigateToScreen,
                    closeDrawer = onDismiss
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(
    currentRoute: String?,
    onNavigateToScreen: (String) -> Unit,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Screen.optionsScreens.forEach { screen ->
            DrawerItem(
                screen = screen,
                selected = currentRoute == screen.route,
                onClick = {
                    onNavigateToScreen(screen.route)
                    closeDrawer()
                }
            )
        }
    }
}

@Composable
private fun DrawerItem(
    screen: Screen.Options,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconRes = when (screen) {
        Screen.Options.Settings -> if (selected) R.drawable.settings_filled else R.drawable.settings_outlined
        Screen.Options.CreateAlarm -> if (selected) R.drawable.create_alarm_filled else R.drawable.create_alarm_outlined
        Screen.Options.ColorPicker -> if (selected) R.drawable.create_color_filled else R.drawable.create_color_outlined
    }
    
    val colors = NavigationDrawerItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onSurface,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    NavigationDrawerItem(
        modifier = Modifier.padding(bottom = 8.dp),
        selected = selected,
        onClick = onClick,
        label = { 
            Text(
                text = screen.title,
                style = if (selected) TextStyles.Default.Bold.M else TextStyles.Default.M
            ) 
        },
        icon = { 
            Icon(
                painter = painterResource(id = iconRes), 
                contentDescription = screen.title, 
                modifier = Modifier.size(Sizes.Icon.M)
            ) 
        },
        colors = colors,
        shape = RoundedCornerShape(topEnd = Sizes.Corner.L, bottomEnd = Sizes.Corner.L)
    )
}
