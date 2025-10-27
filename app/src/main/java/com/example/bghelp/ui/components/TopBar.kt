package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.theme.MainBlue
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun TopBar(
    title: String,
    showWallpaperIcon: Boolean = true,
    onScreenshotClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column {
        // System bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .background(MainBlue)
        )

        // Bar content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MainBlue)
                .height(UI.TOP_BAR_HEIGHT.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wallpaper icon
            if (showWallpaperIcon) {
                IconButton(onClick = onScreenshotClick) {
                    Image(
                        painter = painterResource(id = R.drawable.screenshot),
                        contentDescription = "Wallpaper",
                        modifier = Modifier.size(Sizes.Icon.ExtraLarge)
                    )
                }
            } else {
                // Back icon
                IconButton(onClick = onBackClick) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier.size(Sizes.Icon.ExtraLarge)
                    )
                }
            }

            // Title
            Text(
                text = title,
                style = TextStyles.White.Bold.Large,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Options icon
            IconButton(onClick = onSettingsClick) {
                Image(
                    painter = painterResource(R.drawable.options_inactive),
                    contentDescription = "Options",
                    modifier = Modifier.size(Sizes.Icon.ExtraLarge)
                )
            }
        }
    }
}
