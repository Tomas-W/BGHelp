package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableWithUnboundedRipple
import com.example.bghelp.ui.screens.task.add.TimeField
import com.example.bghelp.ui.screens.task.add.TimeSegment
import com.example.bghelp.ui.theme.ErrorRed
import com.example.bghelp.ui.theme.TextStyles
import java.time.LocalTime
import java.util.Locale

@Composable
fun TimeSelectionInput(
    modifier: Modifier = Modifier,
    time: LocalTime,
    timeField: TimeField,
    activeTimeField: TimeField?,
    activeTimeSegment: TimeSegment?,
    onTimeChanged: (LocalTime) -> Unit,
    onTimeInputClicked: () -> Unit = {},
    onSegmentSelected: (TimeField, TimeSegment) -> Unit,
    onSelectionCleared: () -> Unit,
    keyboardDismissKey: Int = 0,
    isError: Boolean = false
) = WithRipple {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var inputBuffer by remember { mutableStateOf("") }

    val isThisFieldActive = activeTimeField == timeField
    val selectedSegment = if (isThisFieldActive) activeTimeSegment else null

    fun dismissKeyboard() {
        onSelectionCleared()
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    LaunchedEffect(keyboardDismissKey) {
        if (keyboardDismissKey > 0 && selectedSegment != null) {
            dismissKeyboard()
        }
    }

    fun clampHour(value: Int): Int = value.coerceIn(0, 23)
    fun clampMinute(value: Int): Int = value.coerceIn(0, 59)

    fun handleHourInput(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(2)
        inputBuffer = digits
        if (digits.isNotEmpty()) {
            val h = digits.toIntOrNull()?.let { clampHour(it) }
            if (h != null) onTimeChanged(time.withHour(h))
        }
        if (digits.length == 2) {
            onSegmentSelected(timeField, TimeSegment.MINUTE)
            inputBuffer = ""
        }
    }

    fun handleMinuteInput(raw: String) {
        val digits = raw.filter { it.isDigit() }.take(2)
        inputBuffer = digits
        if (digits.isNotEmpty()) {
            val m = digits.toIntOrNull()?.let { clampMinute(it) }
            if (m != null) onTimeChanged(time.withMinute(m))
        }
    }

    // Hidden text field to capture input
    BasicTextField(
        value = inputBuffer,
        onValueChange = { new ->
            when (selectedSegment) {
                TimeSegment.HOUR -> handleHourInput(new)
                TimeSegment.MINUTE -> handleMinuteInput(new)
                null -> {}
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { dismissKeyboard() }
        ),
        modifier = Modifier
            .width(1.dp)
            .height(1.dp)
            .alpha(0f)
            .focusRequester(focusRequester)
    )

    LaunchedEffect(selectedSegment) {
        if (selectedSegment != null) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val errorBackgroundColor = remember { ErrorRed.copy(alpha = 0.15f) }

    // Time select row
    Row(
        modifier = modifier
            // Visuals for wrong endDate
            .clip(RoundedCornerShape(8.dp))
            .background(if (isError) errorBackgroundColor else Color.Transparent)
            .clickable {
                if (selectedSegment != null) {
                    dismissKeyboard()
                }
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hourText = remember(time.hour) { String.format(Locale.getDefault(), "%02d", time.hour) }
        val minuteText = remember(time.minute) { String.format(Locale.getDefault(), "%02d", time.minute) }

        fun select(segment: TimeSegment) {
            onTimeInputClicked()
            if (isThisFieldActive && selectedSegment == segment) {
                dismissKeyboard()
            } else {
                onSegmentSelected(timeField, segment)
                inputBuffer = ""
            }
        }

        Box(
            modifier = Modifier
                .clickableWithUnboundedRipple(onClick = { select(TimeSegment.HOUR) })
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Hour
            Text(
                text = hourText,
                style = if (selectedSegment == TimeSegment.HOUR) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium,
            )
        }

        Text(
            text = ":",
            style = TextStyles.Default.Bold.Medium,
            modifier = Modifier.padding(horizontal = 2.dp),
        )

        Box(
            modifier = Modifier
                .clickableWithUnboundedRipple(onClick = { select(TimeSegment.MINUTE) })
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Minute
            Text(
                text = minuteText,
                style = if (selectedSegment == TimeSegment.MINUTE) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium,
            )
        }
    }
}

