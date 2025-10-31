package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.constants.UiConstants as UI
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
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = statusBarPadding.calculateTopPadding()
    val totalHeight = remember(statusBarHeight) { statusBarHeight + UI.TOP_BAR_HEIGHT.dp }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(UI.TOP_BAR_HEIGHT.dp)
                .padding(horizontal = 8.dp)
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showWallpaperIcon) {
                IconButton(onClick = onScreenshotClick) {
                    Image(
                        painter = painterResource(id = R.drawable.screenshot),
                        contentDescription = "Wallpaper",
                        modifier = Modifier.size(Sizes.Icon.ExtraLarge)
                    )
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier.size(Sizes.Icon.ExtraLarge)
                    )
                }
            }

            Text(
                text = title,
                style = TextStyles.White.Bold.Large,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

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
