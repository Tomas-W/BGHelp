package com.example.bghelp.ui.screens.target

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.data.local.TargetEntity
import com.example.bghelp.ui.screens.task.components.Day
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate
import java.time.Instant


@Composable
fun TargetScreen(targetViewModel: TargetViewModel = hiltViewModel()) {

    val targets by targetViewModel.getAllTargets.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        Column {
            AddTargetButton(targetViewModel = targetViewModel)
            if (targets.isNotEmpty()) {
                Day(targets)
            }
        }
    }
}


@Composable
fun AddTargetButton(targetViewModel: TargetViewModel) {
    Button(
        onClick = {
            val now: Long = Instant.now().epochSecond
            val coords = listOf(Coordinate(53.5, -4.7))
            targetViewModel.addTarget(TargetEntity(
                    date = now,
                    message = "Test Target",
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
        Text("Add Target")
    }
}