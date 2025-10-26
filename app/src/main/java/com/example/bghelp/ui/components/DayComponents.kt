package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.SecondaryBlue
import com.example.bghelp.ui.theme.SecondaryGrey
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.SchedulableItem
import com.example.bghelp.utils.isInFuture
import com.example.bghelp.utils.toDayHeader
import com.example.bghelp.utils.toTaskTime

@Composable
fun <T: SchedulableItem> TimeRow(item: T, onDelete: (T) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Time & Snooze time
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

        // Sound & Vibrate icons
        Row {
            if (item.sound != AlarmMode.OFF) {
                Image(
                    painter = painterResource(R.drawable.sound),
                    contentDescription = item.sound.value,
                    modifier = Modifier.size(Sizes.Icon.Small)
                )
            } else {
                Spacer(modifier = Modifier.size(Sizes.Icon.Small))
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (item.vibrate != AlarmMode.OFF) {
                Image(
                    painter = painterResource(R.drawable.vibrate),
                    contentDescription = item.vibrate.value,
                    modifier = Modifier.size(Sizes.Icon.Small)
                )
            } else {
                Spacer(modifier = Modifier.size(Sizes.Icon.Small))
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.Default.Delete,
                tint = Color.Red,
                contentDescription = "Delete",
                modifier = Modifier.clickable { onDelete(item) }
            )
        }
    }
}

@Composable
fun Message(message: String) {
    Text(
        text = message,
        style = TextStyles.Default.Medium
    )
}

@Composable
fun <T: SchedulableItem> Item(item: T, onDelete: (T) -> Unit) {
    val taskBackgroundColor = if (item.date.isInFuture()) { SecondaryBlue } else { SecondaryGrey }

    SchedulableContainer(
        modifier = Modifier,
        backgroundColor = taskBackgroundColor
    ) {
        TimeRow(item, onDelete)
        Message(item.message)
    }
}

@Composable
fun <T: SchedulableItem> Day(items: List<T>, onDelete: (T) -> Unit) {
    if (items.isEmpty()) {
        return
    }

    Column {
        val taskDate = items[0].date
        MainHeader(taskDate.toDayHeader())
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                Item(item, onDelete)
            }
        }
    }
}
