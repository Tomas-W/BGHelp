package com.example.bghelp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@SuppressLint("RememberInComposition")
@Composable
fun SelectionToggle(
    selectedIndex: Int,
    numberOfStates: Int,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    require(numberOfStates in 2..4) { "numberOfStates must be between 2 and 4" }
    require(selectedIndex in 0 until numberOfStates) { "selectedIndex must be between 0 and ${numberOfStates - 1}" }

    val interactionSource = remember { MutableInteractionSource() }
    val trackColor = MaterialTheme.colorScheme.primary
    val circleSize = 22.dp
    val circleBorderWidth = 4.dp
    val trackHeight = 14.dp
    val circleOverlap = 8.dp
    
    val totalWidth = (circleSize.value * numberOfStates - circleOverlap.value * (numberOfStates - 1)).dp
    
    val containerModifier = Modifier
        .height(circleSize)
        .width(totalWidth)
        .let { modifier ->
            if (onClick != null && enabled) {
                modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
            } else {
                modifier
            }
        }
    
    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(CircleShape)
                .background(trackColor)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(-circleOverlap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(numberOfStates) { index ->
                val isSelected = index == selectedIndex
                
                if (!isSelected) {
                    Box(
                        modifier = Modifier.size(circleSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(
                                    width = circleBorderWidth,
                                    color = trackColor,
                                    shape = CircleShape
                                )
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(circleSize))
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(-circleOverlap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(numberOfStates) { index ->
                val isSelected = index == selectedIndex
                
                if (isSelected) {
                    Box(
                        modifier = Modifier.size(circleSize),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(
                                    width = circleBorderWidth,
                                    color = trackColor,
                                    shape = CircleShape
                                )
                                .background(Color.White, CircleShape)
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(circleSize))
                }
            }
        }
    }
}

@Composable
fun DropdownItem(
    label: String,
    onClick: () -> Unit,
    textStyle: TextStyle
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = textStyle)
    }
}