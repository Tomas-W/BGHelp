package com.example.bghelp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun DrawerScreen(
    currentRoute: String?,
    onNavigateToScreen: (String) -> Unit,
    closeDrawer: () -> Unit
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val totalTop = statusBarHeight + UI.TOP_BAR_HEIGHT.dp
    val totalBottom = navigationBarHeight + UI.BOTTOM_BAR_HEIGHT.dp

    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .padding(
                top = totalTop,
                bottom = totalBottom),
        windowInsets = WindowInsets(0)
    ) {
        Column(modifier = Modifier.padding(top = 24.dp, end = 54.dp)) {
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
                modifier = Modifier.size(Sizes.Icon.XS)
            ) 
        },
        colors = colors,
        modifier = Modifier.padding(bottom = 8.dp),
        shape = RoundedCornerShape(topEnd = Sizes.Corner.L, bottomEnd = Sizes.Corner.L)
    )
}
