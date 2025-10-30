package com.example.bghelp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.SchedulableItem
import com.example.bghelp.ui.theme.TextSizes
import com.example.bghelp.utils.isInFuture
import com.example.bghelp.utils.toTaskTime

@Composable
fun <T: SchedulableItem> DayComponent(
    item: T,
    isExpanded: Boolean,
    onToggleExpanded: (Int) -> Unit,
    onDelete: (T) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val taskBackgroundColor =
        if (item.date.isInFuture()) colorScheme.secondary else colorScheme.tertiary
    val deleteCallback = remember(key1 = item.id) { { onDelete(item) } }
    val toggleCallback = remember(key1 = item.id) { { onToggleExpanded(item.id) } }

    SchedulableContainer(
        modifier = Modifier
            .animateContentSize()
            .clickable(onClick = toggleCallback),
        backgroundColor = taskBackgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeInfo(item = item)
            AlarmIcons(item = item, onDelete = deleteCallback)
        }
        TitleAndDescription(
            title = item.title,
            description = item.description,
            isExpanded = isExpanded
        )
    }
}

@Composable
fun TimeInfo(item: SchedulableItem) {
    Row {
        Text(
            text = item.date.toTaskTime(),
            style = TextStyles.Default.Bold.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (item.snoozeTime > 0) {
            Text(
                text = " +${item.snoozeTime}",
                style = TextStyles.Error.ExtraSmall
            )
        }
    }
}

@Composable
fun TitleAndDescription(
    title: String,
    description: String?,
    isExpanded: Boolean
) {
    Column {
        Text(
            text = title,
            style = TextStyles.Default.Medium
        )
        if (isExpanded && description != null) {
            Text(
                text = description,
                style = TextStyles.Default.Italic.Medium
            )
        } else if (isExpanded) {
            Spacer(modifier = Modifier.height(TextSizes.MEDIUM.dp))
        }
    }
}

@Composable
private fun AlarmIcons(
    item: SchedulableItem,
    onDelete: () -> Unit
) {
    Row {
        SoundIcon(soundMode = item.sound)
        Spacer(modifier = Modifier.width(16.dp))
        VibrateIcon(vibrateMode = item.vibrate)
        Spacer(modifier = Modifier.width(8.dp))
        DeleteIcon(onDelete = onDelete)
    }
}

@Composable
private fun SoundIcon(soundMode: AlarmMode) {
    if (soundMode != AlarmMode.OFF) {
        Image(
            painter = painterResource(R.drawable.sound),
            contentDescription = soundMode.value,
            modifier = Modifier.size(Sizes.Icon.Small)
        )
    } else {
        Spacer(modifier = Modifier.size(Sizes.Icon.Small))
    }
}

@Composable
private fun VibrateIcon(vibrateMode: AlarmMode) {
    if (vibrateMode != AlarmMode.OFF) {
        Image(
            painter = painterResource(R.drawable.vibrate),
            contentDescription = vibrateMode.value,
            modifier = Modifier.size(Sizes.Icon.Small)
        )
    } else {
        Spacer(modifier = Modifier.size(Sizes.Icon.Small))
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
