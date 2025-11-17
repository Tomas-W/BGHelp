package com.example.bghelp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextBlack
import com.example.bghelp.ui.theme.TextGrey
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.navigation.Screen

@Composable
fun OptionsDrawer(
    visible: Boolean,
    currentRoute: String?,
    onNavigateToScreen: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarHeight = UI.BOTTOM_BAR_HEIGHT.dp
    val bottomOffset = navigationBarHeight + bottomBarHeight
    
    // Backdrop that fades in/out independently
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss)
        )
    }
    
    // Drawer content that slides up from bottom
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                animationSpec = tween(300),
                initialOffsetY = { it }
            ),
            exit = slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { it }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
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
        selectedIconColor = TextBlack,
        selectedTextColor = TextBlack,
        unselectedIconColor = TextGrey,
        unselectedTextColor = TextGrey
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
