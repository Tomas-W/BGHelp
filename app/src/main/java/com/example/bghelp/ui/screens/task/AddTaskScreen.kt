package com.example.bghelp.ui.screens.task

import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.R
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.screens.options.CreateAlarmViewModel
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddTaskScreen(
    viewModel: CreateAlarmViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()

    var selected by remember { mutableIntStateOf(0) }

    MainContentContainer(
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row {
                Icon(
                    painter = painterResource(R.drawable.events_outlined),
                    contentDescription = "All day"
                )

                Text("All day")
            }


            TwoOptionToggle(
                selectedIndex = selected,
                onOptionSelected = { selected = it }
            )
        }




        Spacer(modifier = Modifier.height(120.dp))

        SelectDayField(
            selectedDate = selectedDate,
            onDateSelected = { date -> viewModel.updateSelectedDate(date) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SelectTimeField(
            selectedTime = selectedTime,
            onTimeSelected = { time -> viewModel.updateSelectedTime(time) }
        )

        // Title
        // Field input
//        val title = "Title"
//        var text by remember { mutableStateOf("") }
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth(),
//            value = text,
//            onValueChange = { text = it },
//            label = { Text(text = title) }
//        )

        // Description
        // Field input

        // Alarm          Selection dropdown (Wave, Horn ect)
        //                Off   Once    Continuous
        // Sound           0------------------  (slider)
        // Vibrate          ------------------0 (slider)

        // Cancel          Create
    }
}

@Composable
fun TwoOptionToggle(
    options: List<String> = listOf("Left", "Right"),
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    val backgroundColor = Color(0xFFE0E0E0)
    val activeColor = Color(0xFF2196F3)
    val cornerRadius = 24.dp
    val totalWidth = 240.dp
    val segmentWidth = totalWidth / options.size

    val animatedOffset by animateDpAsState(
        targetValue = selectedIndex * segmentWidth,
        label = "offset"
    )

    // Outer Box defines BoxScope (so .align works)
    Box(
        modifier = Modifier
            .width(totalWidth)
            .height(48.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // Wrap the sliding indicator in another BoxScope
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(segmentWidth)
                    .offset(x = animatedOffset)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(activeColor)
                    .align(Alignment.CenterStart) // ✅ Works — we’re inside a BoxScope now
            )
        }

        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val textColor by animateColorAsState(
                    targetValue = if (selectedIndex == index) Color.White else Color.Black,
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onOptionSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = textColor, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun SelectDayField(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dateDialogState = rememberMaterialDialogState()
    val formatter = remember { DateTimeFormatter.ofPattern("EEEE MMMM dd") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Day",
                style = TextStyles.Grey.Bold.Large
            )

            Text(
                text = selectedDate.format(formatter),
                style = TextStyles.Default.Italic.Small
            )
        }

        IconButton(onClick = { dateDialogState.show() }) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.MegaLarge),
                painter = painterResource(R.drawable.events_outlined),
                contentDescription = "Calendar"
            )
        }
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            initialDate = selectedDate,
            title = "Select a date",
            allowedDateValidator = { date -> date.isAfter(LocalDate.now().minusDays(1)) }
        ) { date ->
            onDateSelected(date)
        }
    }
}

@Composable
fun SelectTimeField(
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    val timeDialogState = rememberMaterialDialogState()
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Time",
                style = TextStyles.Grey.Bold.Large
            )

            Text(
                text = selectedTime.format(formatter),
                style = TextStyles.Default.Italic.Small
            )
        }

        IconButton(onClick = { timeDialogState.show() }) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.MegaLarge),
                painter = painterResource(R.drawable.events_outlined),
                contentDescription = "Clock"
            )
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        timepicker(
            initialTime = selectedTime,
            title = "Select a time",
            is24HourClock = true
        ) { time ->
            onTimeSelected(time)
        }
    }
}
