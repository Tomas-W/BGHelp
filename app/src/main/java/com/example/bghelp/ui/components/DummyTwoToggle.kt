package com.example.bghelp.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("RememberInComposition")
@Composable
fun DummyTwoToggle(
    selectedIndex: Int,
    enabled: Boolean = true,
    onOverlayClick: (() -> Unit)? = null,
) {
    val isChecked = selectedIndex == 1

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val trackWidth = 40.dp
        val trackHeight = 24.dp
        val thumbSize = 14.dp
        val horizontalPadding = 5.dp

        val maxOffset = trackWidth - horizontalPadding * 2 - thumbSize
        val thumbOffset by animateDpAsState(
            targetValue = if (isChecked) maxOffset else 0.dp,
            label = "DummyToggleThumbOffset"
        )

        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.CenterStart
        ) {
            // Optional subtle border tint (visual parity with SecondaryGrey border)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            )

            Box(
                modifier = Modifier
                    .padding(start = horizontalPadding)
                    .offset(x = thumbOffset)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            if (onOverlayClick != null && enabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) { onOverlayClick() }
                )
            }
        }
    }
}