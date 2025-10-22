package com.example.bghelp.ui.screens.target

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.ui.components.Day
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate
import java.time.Instant
import java.time.ZoneId


@Composable
fun TargetScreen(targetViewModel: TargetViewModel = hiltViewModel()) {
    val targets by targetViewModel.targetsInRange.collectAsState(initial = emptyList())
//    val allTargets by targetViewModel.getAllTargets.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        Column {
            AddButtonRow(targetViewModel)
            if (targets.isNotEmpty()) {
                Day(targets, onDelete = { target -> targetViewModel.deleteTarget(target) })
            }
        }
    }
}


@Composable
fun AddButtonRow(targetViewModel: TargetViewModel) {
    val now: Long = 0
    val oneMinute: Long = 60 * 1000  // 60 seconds in milliseconds
    val oneHour: Long = 60 * 60 * 1000  // 1 hour in milliseconds
    val oneDay: Long = 24 * 60 * 60 * 1000  // 1 day in milliseconds
    val oneWeek: Long = 7 * 24 * 60 * 60 * 1000  // 1 week in milliseconds
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AddTargetButton(targetViewModel = targetViewModel, time = now, text = "Now")
        AddTargetButton(targetViewModel = targetViewModel, time = oneMinute, text = "Min")
        AddTargetButton(targetViewModel = targetViewModel, time = oneHour, text = "Hour")
        AddTargetButton(targetViewModel = targetViewModel, time = oneDay, text = "Day")
    }
}


@Composable
fun AddTargetButton(
    targetViewModel: TargetViewModel,
    time: Long,
    text: String
) {
    Button(
        onClick = {
            val now: Long = Instant.now().toEpochMilli()  // Use milliseconds
            val date = now + time
            val localDateTime = Instant.ofEpochMilli(date)  // Use ofEpochMilli
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            val coords = listOf(Coordinate(53.5, -4.7))
            targetViewModel.addTarget(
                CreateTarget(
                    date = localDateTime,
                    message = "Test Target $date",
                    expired = false,
                    coordinates = coords,
                    alertDistance = 300,
                    alarmName = "whatever",
                    sound = AlarmMode.ONCE,
                    vibrate = AlarmMode.CONTINUOUS,
                    snoozeTime = 0
                )
            )
        }
        ) {
        Text(text)
    }
}