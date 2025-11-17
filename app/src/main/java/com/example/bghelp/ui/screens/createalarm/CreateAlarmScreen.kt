package com.example.bghelp.ui.screens.createalarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.R
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.screens.createalarm.CreateAlarmViewModel
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
fun CreateAlarmScreen(
    viewModel: CreateAlarmViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()

    MainContentContainer(
    ) {
        Spacer(modifier = Modifier.height(12.dp))

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
                style = TextStyles.Grey.Bold.L
            )

            Text(
                text = selectedDate.format(formatter),
                style = TextStyles.Default.Italic.S
            )
        }

        IconButton(onClick = { dateDialogState.show() }) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.XXL),
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
                style = TextStyles.Grey.Bold.L
            )

            Text(
                text = selectedTime.format(formatter),
                style = TextStyles.Default.Italic.S
            )
        }

        IconButton(onClick = { timeDialogState.show() }) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.XXL),
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
