package com.example.bghelp.ui.screens.task.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bghelp.R
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Task
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.theme.TextSizes
import com.example.bghelp.utils.formatSnoozeDuration
import com.example.bghelp.utils.toTaskTime

@Composable
fun DayComponent(
    modifier: Modifier = Modifier,
    task: Task,
    isExpanded: Boolean,
    onToggleExpanded: (Int) -> Unit,
    onDelete: (Task) -> Unit,
    onLongPress: ((Task) -> Unit)? = null,
    isPendingDeletion: Boolean = false,
    onCancelDeletion: () -> Unit = {},
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val taskBackgroundColor = if (!task.expired) {
        task.color.toComposeColor()
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val deleteCallback = remember(key1 = task.id) { { onDelete(task) } }
    val toggleCallback = remember(key1 = task.id) { { onToggleExpanded(task.id) } }
    val longPressCallback = remember(key1 = task.id) { 
        if (onLongPress != null) {
            { onLongPress!!.invoke(task) }
        } else {
            null
        }
    }
    
    val cornerRadius = remember {
        Sizes.Corner.S
    }
    val shape = remember(isFirst, isLast, cornerRadius) {
        when {
            isFirst && isLast -> RoundedCornerShape(cornerRadius)
            isFirst -> RoundedCornerShape(topEnd = cornerRadius)
            isLast -> RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
            else -> null
        }
    }

    Box(modifier = modifier) {
        DayContainer(
            modifier = Modifier
//                .blur(if (isPendingDeletion) 3.dp else 0.dp)
                .alpha(if (isPendingDeletion) 0.4f else 1f)
                .animateContentSize()
                .let { mod ->
                    longPressCallback?.let { longPress ->
                        mod.combinedClickable(
                            onClick = toggleCallback,
                            onLongClick = longPress
                        )
                    } ?: mod.clickable(onClick = toggleCallback)
                },
            backgroundColor = taskBackgroundColor,
            shape = shape
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeInfo(task = task)
                AlarmIcons(task = task, onDelete = deleteCallback)
            }

            TitleAndDescription(
                title = task.title,
                description = task.description,
                isExpanded = isExpanded
            )
            if (isExpanded) {
                val imageAttachment = task.image
                if (imageAttachment?.uri != null) {
                    Spacer(modifier = Modifier.height(Sizes.Icon.XXS))
                    TaskImagePreview(
                        imagePath = imageAttachment.uri,
                        displayName = imageAttachment.displayName
                    )
                }
            }
        }
        
        // Deletion overlay
        if (isPendingDeletion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .let { if (shape != null) it.clip(shape) else it }
                    .clickable(onClick = onCancelDeletion),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CANCEL",
                    fontSize = 56.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp
                )
            }
        }
    }
    if (isLast) Spacer(modifier = Modifier.height(6.dp))
}

@Composable
fun DayContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    shape: Shape? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (shape != null) it.clip(shape) else it }
            .let { if (backgroundColor != null) it.background(backgroundColor) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        content = content
    )
}

@Composable
private fun TimeInfo(task: Task) {
    Row {
        Text(
            text = task.date.toTaskTime(),
            style = TextStyles.Default.Bold.M,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (task.snoozeSeconds > 0) {
            Text(
                text = " +${task.snoozeSeconds.formatSnoozeDuration()}",
                style = TextStyles.Error.XS
            )
        }
    }
}

@Composable
private fun TitleAndDescription(
    title: String,
    description: String?,
    isExpanded: Boolean
) {
    Column {
        Text(
            text = title,
            style = TextStyles.Default.M
        )
        if (isExpanded && description != null) {
            Text(
                text = description,
                style = TextStyles.Default.Italic.M
            )
        } else if (isExpanded) {
            Spacer(modifier = Modifier.height(TextSizes.M.dp))
        }
    }
}

@Composable
private fun AlarmIcons(
    task: Task,
    onDelete: () -> Unit
) {
    Row {
        SoundIcon(soundMode = task.sound)
        Spacer(modifier = Modifier.width(16.dp))
        VibrateIcon(vibrateMode = task.vibrate)
        Spacer(modifier = Modifier.width(8.dp))
        DeleteIcon(onDelete = onDelete)
    }
}

@Composable
private fun SoundIcon(soundMode: AlarmMode) {
    val iconRes = when (soundMode) {
        AlarmMode.OFF -> null
        AlarmMode.ONCE -> R.drawable.sound_once
        AlarmMode.CONTINUOUS -> R.drawable.sound_continuous
    }

    if (iconRes != null) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = soundMode.value,
            modifier = Modifier.size(Sizes.Icon.S)
        )
    } else {
        Spacer(modifier = Modifier.size(Sizes.Icon.S))
    }
}

@Composable
private fun VibrateIcon(vibrateMode: AlarmMode) {
    val iconRes = when (vibrateMode) {
        AlarmMode.OFF -> null
        AlarmMode.ONCE -> R.drawable.vibrate_once
        AlarmMode.CONTINUOUS -> R.drawable.vibrate_continuous
    }

    if (iconRes != null) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = vibrateMode.value,
            modifier = Modifier.size(Sizes.Icon.S)
        )
    } else {
        Spacer(modifier = Modifier.size(Sizes.Icon.S))
    }
}

@Composable
private fun DeleteIcon(onDelete: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Delete,
        tint = Color.Red,
        contentDescription = "Delete",
        modifier = Modifier.clickable(onClick = onDelete)
    )
}

@Composable
fun TaskPreviewComponent(task: Task) {
    val taskBackgroundColor = if (!task.expired) {
        task.color.toComposeColor()
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    
    DayContainer(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = taskBackgroundColor,
        shape = RoundedCornerShape(Sizes.Corner.S)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeInfo(task = task)
            AlarmIconsPreview(task = task)
        }
        
        TitleAndDescription(
            title = task.title,
            description = task.description,
            isExpanded = false
        )
    }
}

@Composable
private fun AlarmIconsPreview(task: Task) {
    Row {
        SoundIcon(soundMode = task.sound)
        Spacer(modifier = Modifier.width(16.dp))
        VibrateIcon(vibrateMode = task.vibrate)
    }
}

