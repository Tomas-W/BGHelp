package com.example.bghelp.ui.screens.target

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Target
import com.example.bghelp.ui.components.SchedulableContainer
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextBold
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.theme.lTextItalic
import com.example.bghelp.ui.theme.mTextDefault
import com.example.bghelp.ui.theme.sTextDefault
import com.example.bghelp.utils.isInFuture
import com.example.bghelp.utils.toTaskTime

@Composable
fun TargetComponent(
    target: Target,
    isExpanded: Boolean,
    onToggleExpanded: (Int) -> Unit,
    onDelete: (Target) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor =
        if (target.date.isInFuture()) colorScheme.secondary else colorScheme.tertiary
    val deleteCallback = remember(key1 = target.id) { { onDelete(target) } }
    val toggleCallback = remember(key1 = target.id) { { onToggleExpanded(target.id) } }

    SchedulableContainer(
        modifier = Modifier
            .animateContentSize()
            .clickable(onClick = toggleCallback),
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TargetTimeInfo(target = target)
            TargetIcons(target = target, onDelete = deleteCallback)
        }
        TargetDetails(
            title = target.title,
            description = target.description,
            coordinatesCount = target.coordinates.size,
            alertDistance = target.alertDistance,
            isExpanded = isExpanded
        )
    }
}

@Composable
private fun TargetTimeInfo(target: Target) {
    Row {
        Text(
            text = target.date.toTaskTime(),
            style = MaterialTheme.typography.lTextBold,
            modifier = Modifier.padding(end = 8.dp)
        )
        if (target.snoozeTime > 0) {
            Text(
                text = " +${target.snoozeTime}",
                style = MaterialTheme.typography.sTextDefault,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun TargetDetails(
    title: String,
    description: String?,
    coordinatesCount: Int,
    alertDistance: Int,
    isExpanded: Boolean
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.lTextDefault
        )
        if (isExpanded && description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.lTextItalic
            )
        } else if (isExpanded) {
            Spacer(modifier = Modifier.height(Sizes.Icon.L))
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(Sizes.Size.S))
            Text(
                text = "Locations: $coordinatesCount",
                style = MaterialTheme.typography.mTextDefault,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Sizes.Size.XS))
            Text(
                text = "Alert distance: ${alertDistance}m",
                style = MaterialTheme.typography.mTextDefault,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TargetIcons(
    target: Target,
    onDelete: () -> Unit
) {
    Row {
        SoundIcon(soundMode = target.sound)
        Spacer(modifier = Modifier.width(16.dp))
        VibrateIcon(vibrateMode = target.vibrate)
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
        tint = MaterialTheme.colorScheme.error,
        contentDescription = "Delete",
        modifier = Modifier.clickable(onClick = onDelete)
    )
}

