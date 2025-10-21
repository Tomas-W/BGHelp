package com.example.bghelp.ui.screens.task.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.theme.SecondaryBlue
import com.example.bghelp.ui.theme.SecondaryGrey
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.SchedulableItem
import com.example.bghelp.utils.isInFuture
import com.example.bghelp.utils.toDayHeader
import com.example.bghelp.utils.toTaskTime


@Composable
fun <T: SchedulableItem> TimeRow(entity: T) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = entity.date.toTaskTime(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            if (entity.snoozeTime > 0) {
                Text(
                    text = " +${entity.snoozeTime}",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }

        Row() {
            if (entity.sound != AlarmMode.OFF) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.Black,
                    contentDescription = "Play"
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (entity.vibrate != AlarmMode.OFF) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    tint = Color.Black,
                    contentDescription = "Menu"
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun Message(message: String) {
    Text(
        text = message,
        fontSize = 18.sp,
        color = Color.Black
    )
}

@Composable
fun <T: SchedulableItem> Item(entity: T) {
    val taskBackgroundColor = if (entity.date.isInFuture()) { SecondaryBlue } else { SecondaryGrey }

    Column(
        modifier = Modifier
            .background(taskBackgroundColor)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
    ) {
        TimeRow(entity)
        Message(entity.message)
    }
}

@Composable
fun <T: SchedulableItem> Day(entities: List<T>) {
    if (entities.isEmpty()) {
        return
    }

    Column {
        val taskDate = entities[0].date
        MainHeader(taskDate.toDayHeader())
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(entities) { entity ->
                Item(entity)
            }
        }
    }
}
