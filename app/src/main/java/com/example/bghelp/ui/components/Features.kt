package com.example.bghelp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import com.example.bghelp.ui.utils.clickableDismissFocus
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.bghelp.constants.UiConstants as UI

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
                modifier.clickableDismissFocus(
                    enabled = enabled,
                    onClick = onClick
                )
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

private fun Modifier.cropVertical(vertical: Dp): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val verticalPx = vertical.toPx().toInt()
    
    layout(placeable.width, placeable.height - (verticalPx * 2)) {
        placeable.placeRelative(0, -verticalPx)
    }
}

@Composable
fun CustomDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    heightMultiplier: Float = 1f,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable () -> Unit
) {
    val maxHeight = remember(heightMultiplier) {
        heightMultiplier * UI.DROPDOWN_HEIGHT
    }
    
    DropdownMenu(
        modifier = modifier
            .cropVertical(8.dp)
            .heightIn(max = maxHeight.dp)
            .background(MaterialTheme.colorScheme.tertiary),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset
    ) {
        content()
    }
}

@Composable
fun DropdownItem(
    label: String,
    onClick: () -> Unit,
    textStyle: TextStyle,
    spacing: Dp
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            // .height(itemHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = spacing / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = textStyle)
    }
}