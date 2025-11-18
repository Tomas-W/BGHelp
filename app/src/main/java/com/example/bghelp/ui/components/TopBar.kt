package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.bottomBorder

@Composable
fun TopBar(
    title: String,
    showWallpaperIcon: Boolean = true,
    onScreenshotClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(MaterialTheme.colorScheme.background)
            .bottomBorder(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showWallpaperIcon) {
            IconButton(onClick = onScreenshotClick) {
                Image(
                    painter = painterResource(id = R.drawable.wallpaper),
                    contentDescription = "Go to wallpaper screen",
                    modifier = Modifier.size(Sizes.Icon.L)
                )
            }
        } else {
            IconButton(onClick = onBackClick) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Go to previous screen",
                    modifier = Modifier.size(Sizes.Icon.L)
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
