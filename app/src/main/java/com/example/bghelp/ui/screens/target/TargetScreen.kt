package com.example.bghelp.ui.screens.target

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.ui.components.LazyColumnContainer
import com.example.bghelp.ui.components.MainHeader
import com.example.bghelp.ui.components.DayComponent
import com.example.bghelp.ui.components.WeekNavigationRow
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Coordinate
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.utils.toDayHeader
import java.time.LocalDateTime

@SuppressLint("FrequentlyChangingValue")
@Composable
fun TargetScreen(targetViewModel: TargetViewModel = hiltViewModel()) {
    // Target items
    val selectedTargets by targetViewModel.targetsInRange.collectAsState(initial = emptyList())
    val selectedDate by targetViewModel.selectedDate.collectAsState()
    val expandedTargetIds by targetViewModel.expandedTargetIds.collectAsState()
    // Week nav
    val navSelectedDate by remember { derivedStateOf { selectedDate.toLocalDate() } }
    val navMonthYear by targetViewModel.monthYear.collectAsState()
    val navWeekNumber by targetViewModel.weekNumber.collectAsState()
    val navWeekDays by targetViewModel.weekDays.collectAsState()
    // Scroll
    val listState = rememberLazyListState()
    val savedScrollIndex = targetViewModel.getSavedScrollIndex()
    val savedScrollOffset = targetViewModel.getSavedScrollOffset()
    // Return to old position
    LaunchedEffect(selectedTargets) {
        if (selectedTargets.isNotEmpty() && savedScrollIndex > 0) {
            listState.scrollToItem(savedScrollIndex, savedScrollOffset)
        }
    }
    // Save current position
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        targetViewModel.saveScrollPosition(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
        ) {
        AddButtonRow(targetViewModel)

        WeekNavigationRow(
            monthYear = navMonthYear,
            weekNumber = navWeekNumber,
            weekDays = navWeekDays,
            selectedDate = navSelectedDate,
            onPreviousWeek = { targetViewModel.goToPreviousWeek() },
            onNextWeek = { targetViewModel.goToNextWeek() },
            onDaySelected = targetViewModel::goToDay,
        )
        MainContentContainer {
            MainHeader(selectedDate.toDayHeader())

            LazyColumnContainer(state = listState) {
                if (selectedTargets.isNotEmpty()) {
                    items(
                        items = selectedTargets,
                        key = { target -> target.id },
                        contentType = { "target" }
                        ) { target ->
                        DayComponent(
                            item = target,
                            isExpanded = expandedTargetIds.contains(target.id),
                            onToggleExpanded = targetViewModel::toggleTargetExpanded,
                            onDelete = targetViewModel::deleteTarget
                        )
                    }
                } else {
                    item {
                        EmptyTarget()
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTarget() {
    Text("No targets for this day")
}

@Composable
fun AddButtonRow(targetViewModel: TargetViewModel) {
    val oneHour = remember { 60 * 60 * 1000L }
    val oneDay = remember { 24 * 60 * 60 * 1000L }
    val threeDays = remember { 3 * 24 * 60 * 60 * 1000L }
    val oneWeek = remember { 7 * 24 * 60 * 60 * 1000L }

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AddTargetButton(targetViewModel = targetViewModel, time = oneHour, text = "Hour")
        AddTargetButton(targetViewModel = targetViewModel, time = oneDay, text = "Day")
        AddTargetButton(targetViewModel = targetViewModel, time = threeDays, text = "3 Days")
        AddTargetButton(targetViewModel = targetViewModel, time = oneWeek, text = "Week")
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
            val now = LocalDateTime.now()
            val localDateTime = now.plusSeconds(time / 1000)
            val coords = listOf(Coordinate(53.5, -4.7))
            targetViewModel.addTarget(
                CreateTarget(
                    date = localDateTime,
                    title = "Test Target",
                    description = "Created at $localDateTime",
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
