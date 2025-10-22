package com.example.bghelp.ui.components

import androidx.compose.foundation.background
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
import com.example.bghelp.ui.theme.SecondaryBlue
import com.example.bghelp.ui.theme.SecondaryGrey
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.SchedulableItem
import com.example.bghelp.utils.isInFuture
import com.example.bghelp.utils.toDayHeader
import com.example.bghelp.utils.toTaskTime


@Composable
fun <T: SchedulableItem> TimeRow(item: T, onDelete: (T) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = item.date.toTaskTime(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )
            if (item.snoozeTime > 0) {
                Text(
                    text = " +${item.snoozeTime}",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }

        Row() {
            if (item.sound != AlarmMode.OFF) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color.Black,
                    contentDescription = "Play"
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (item.vibrate != AlarmMode.OFF) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    tint = Color.Black,
                    contentDescription = "Menu"
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
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
        fontSize = 18.sp,
        color = Color.Black
    )
}

@Composable
fun <T: SchedulableItem> Item(item: T, onDelete: (T) -> Unit) {
    val taskBackgroundColor = if (item.date.isInFuture()) { SecondaryBlue } else { SecondaryGrey }

    Column(
        modifier = Modifier
            .background(taskBackgroundColor)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxWidth()
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
