package com.example.bghelp.ui.screens.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.bghelp.R
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import com.example.bghelp.ui.components.DummyTwoToggle
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.YearMonth
import java.time.Month
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.times
import com.example.bghelp.ui.theme.BGHelpTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.DpOffset
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableWithCalendarRipple
import com.example.bghelp.ui.components.clickableWithUnboundedRipple
import java.util.Locale

private enum class WhenSelection(val index: Int) {
    AT_TIME(0),
    ALL_DAY(1)
}

private object AddTaskConstants {
    val SPACER_HEIGHT = 12.dp
    val SPACER_HALF_HEIGHT = 6.dp
    val ICON_SPACER_WIDTH = 24.dp
    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100
}

@Composable
fun AddTaskScreen() {
    var whenSelection by remember { mutableStateOf(WhenSelection.AT_TIME) }
    val whenStringChoices = remember { listOf("At time", "All day") }
    val whenIconChoices = remember { listOf(R.drawable.at_time, R.drawable.all_day) }

    MainContentContainer {
        // All day / At time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 24.dp)
                .clickable { 
                    whenSelection = when (whenSelection) {
                        WhenSelection.AT_TIME -> WhenSelection.ALL_DAY
                        WhenSelection.ALL_DAY -> WhenSelection.AT_TIME
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.Medium),
                painter = painterResource(whenIconChoices[whenSelection.index]),
                contentDescription = whenStringChoices[whenSelection.index]
            )

            Spacer(modifier = Modifier.width(AddTaskConstants.ICON_SPACER_WIDTH))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = whenStringChoices[whenSelection.index],
                    style = TextStyles.Default.Medium
                )

                DummyTwoToggle(
                    selectedIndex = whenSelection.index,
                    enabled = true,
                    onOverlayClick = { 
                        whenSelection = when (whenSelection) {
                            WhenSelection.AT_TIME -> WhenSelection.ALL_DAY
                            WhenSelection.ALL_DAY -> WhenSelection.AT_TIME
                        }
                    }
                )
            }
        }

        AddTaskSpacer()
        DateSelection(whenSelection = whenSelection.index)

        AddTaskDivider()
    }
}

@Composable
fun AddTaskSpacer() {
    Spacer(modifier = Modifier.height(AddTaskConstants.SPACER_HEIGHT))
}

@Composable
fun AddTaskSpacerHalf() {
    Spacer(modifier = Modifier.height(AddTaskConstants.SPACER_HALF_HEIGHT))
}

private enum class TimeSegment { HOUR, MINUTE }

@Composable
private fun TimeSelectionInput(
    time: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    onTimeInputClicked: () -> Unit = {},
    keyboardDismissKey: Int = 0,
    modifier: Modifier = Modifier
) = WithRipple {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var selectedSegment by remember { mutableStateOf<TimeSegment?>(null) }
    var inputBuffer by remember { mutableStateOf("") }

    fun dismissKeyboard() {
        selectedSegment = null
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

    // Time select row
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable {
                if (selectedSegment != null) {
                    dismissKeyboard()
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val hourText = remember(time.hour) { String.format(Locale.getDefault(), "%02d", time.hour) }
        val minuteText = remember(time.minute) { String.format(Locale.getDefault(), "%02d", time.minute) }

        fun select(segment: TimeSegment) {
            onTimeInputClicked()
            if (selectedSegment == segment) {
                dismissKeyboard()
            } else {
                selectedSegment = segment
                inputBuffer = ""
            }
        }

        Box(
            modifier = Modifier
                .clickableWithUnboundedRipple(onClick = { select(TimeSegment.HOUR) })
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Hour
            Text(
                text = hourText,
                style = if (selectedSegment == TimeSegment.HOUR) TextStyles.MainBlue.Bold.Large else TextStyles.Default.Large,
            )
        }

        Text(
            text = ":",
            style = TextStyles.Default.Large,
            modifier = Modifier.padding(horizontal = 6.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .clickableWithUnboundedRipple(onClick = { select(TimeSegment.MINUTE) })
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Minute
            Text(
                text = minuteText,
                style = if (selectedSegment == TimeSegment.MINUTE) TextStyles.MainBlue.Bold.Large else TextStyles.Default.Large,
            )
        }
    }
}

@Composable
fun AddTaskDivider() {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(12.dp))
}

enum class DateField { START, END }

@Composable
fun DateRangeCalendar(
    currentMonth: YearMonth,
    startDate: LocalDate,
    endDate: LocalDate,
    onDayClicked: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) = WithRipple {
    val fMonthLong = remember { DateTimeFormatter.ofPattern("MMMM") }
    val fYarLong = remember { DateTimeFormatter.ofPattern("yyyy") }
    val firstDayOfWeek = DayOfWeek.MONDAY
    val weeks = remember(currentMonth, firstDayOfWeek) { buildMonthWeeks(currentMonth, firstDayOfWeek) }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isYearMenuOpen by remember { mutableStateOf(false) }
    val months = remember { Month.values().toList() }
    val years = remember { (AddTaskConstants.MIN_YEAR..AddTaskConstants.MAX_YEAR).toList() }
    val dropdownHeight = 6 * 48.dp

    Column(modifier = modifier.fillMaxWidth()) {
        // Header | Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month
            IconButton(
                modifier = Modifier
                    .size(Sizes.Icon.Large),
                onClick = { onMonthChanged(currentMonth.minusMonths(1)) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = "Previous month"
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Month dropdown
                Box {
                    Text(
                        text = currentMonth.format(fMonthLong),
                        style = TextStyles.Grey.Bold.Large,
                        modifier = Modifier.clickable { isMonthMenuOpen = true }
                    )

                    DropdownMenu(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary)
                            .heightIn(max = dropdownHeight),
                        expanded = isMonthMenuOpen,
                        onDismissRequest = { isMonthMenuOpen = false },
                        offset = DpOffset(x = 0.dp, y = 4.dp)
                    ) {
                        months.forEach { m ->
                            val label = YearMonth.of(currentMonth.year, m).format(fMonthLong)
                            DropdownItem(
                                label = label,
                                onClick = {
                                    onMonthChanged(YearMonth.of(currentMonth.year, m))
                                    isMonthMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(AddTaskConstants.ICON_SPACER_WIDTH))

                // Year dropdown
                Box {
                    Text(
                        text = currentMonth.format(fYarLong),
                        style = TextStyles.Grey.Bold.Large,
                        modifier = Modifier.clickable { isYearMenuOpen = true }
                    )

                    DropdownMenu(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary)
                            .heightIn(max = dropdownHeight),
                        expanded = isYearMenuOpen,
                        onDismissRequest = { isYearMenuOpen = false },
                        offset = DpOffset(x = 0.dp, y = 4.dp)
                    ) {
                        years.forEach { y ->
                            val label = YearMonth.of(y, currentMonth.month).format(fYarLong)
                            DropdownItem(
                                label = label,
                                onClick = {
                                    onMonthChanged(YearMonth.of(y, currentMonth.month))
                                    isYearMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            // Next month
            IconButton(
                modifier = Modifier
                    .size(Sizes.Icon.Large),
                onClick = { onMonthChanged(currentMonth.plusMonths(1)) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "Next month"
                )
            }
        }

        AddTaskSpacerHalf()

        // Days of week header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (dow in dayOfWeekOrder(firstDayOfWeek)) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = dow.name.take(1),
                        style = TextStyles.Grey.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weeks grid
        val rangeStart = minOf(startDate, endDate)
        val rangeEnd = maxOf(startDate, endDate)

        Column(modifier = Modifier.fillMaxWidth()) {
            weeks.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    week.forEach { day ->
                        val inCurrentMonth = day.month == currentMonth.month
                        val isStart = day == startDate
                        val isEnd = day == endDate
                        val inRange = !day.isBefore(rangeStart) && !day.isAfter(rangeEnd)

                        val rangeBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        val startEndBgColor = MaterialTheme.colorScheme.primary
                        val startEndTextColor = MaterialTheme.colorScheme.onPrimary

                        // Day container
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = if (inRange && !isStart && !isEnd) rangeBgColor else Color.Transparent
                                )
                                .clickableWithCalendarRipple(onClick = { onDayClicked(day) }),
                            contentAlignment = Alignment.Center
                        ) {
                            // Start/End highlight
                            if (isStart || isEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(startEndBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        style = TextStyles.Default.Bold.Medium,
                                        color = startEndTextColor,
                                    )
                                }
                            } else {
                                Text(
                                    text = day.dayOfMonth.toString(),
                                    style = TextStyles.Default.Medium,
                                    color = if (inCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun DateSelection(
    whenSelection: Int,
    modifier: Modifier = Modifier
) {
    val fMonthYearDayShort = remember { DateTimeFormatter.ofPattern("EEE, MMM d") }
    var dateStartSelection by remember { mutableStateOf(LocalDate.now()) }
    var dateEndSelection by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var activeDateField by remember { mutableStateOf<DateField?>(null) }
    var isCalendarVisible by remember { mutableStateOf(false) }
    var keyboardDismissKey by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        // dateStart | dateEnd
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(Sizes.Icon.Medium))
                Spacer(modifier = Modifier.width(AddTaskConstants.ICON_SPACER_WIDTH))

                Text(
                    modifier = Modifier.clickable {
                        if (isCalendarVisible && activeDateField == DateField.START) {
                            isCalendarVisible = false
                            activeDateField = null
                            keyboardDismissKey++
                        } else {
                            activeDateField = DateField.START
                            isCalendarVisible = true
                        }
                    },
                    text = dateStartSelection.format(fMonthYearDayShort),
                    style = if (activeDateField == DateField.START) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    modifier = Modifier.size(Sizes.Icon.Small),
                    painter = painterResource(R.drawable.double_arrow_right),
                    contentDescription = "Till"
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    modifier = Modifier
                        .width(120.dp)
                        .clickable {
                            if (isCalendarVisible && activeDateField == DateField.END) {
                                isCalendarVisible = false
                                activeDateField = null
                                keyboardDismissKey++
                            } else {
                                activeDateField = DateField.END
                                isCalendarVisible = true
                            }
                        },
                    text = dateEndSelection.format(fMonthYearDayShort),
                    style = if (activeDateField == DateField.END) TextStyles.MainBlue.Bold.Medium else TextStyles.Default.Medium
                )
            }
        }

        AddTaskSpacer()

        if (isCalendarVisible && activeDateField != null) {
            DateRangeCalendar(
                currentMonth = currentMonth,
                startDate = dateStartSelection,
                endDate = dateEndSelection,
                onDayClicked = { clickedDate ->
                    when (activeDateField) {
                        DateField.START -> {
                            dateStartSelection = clickedDate
                            if (clickedDate.isAfter(dateEndSelection)) {
                                dateEndSelection = clickedDate
                            }
                        }
                        DateField.END -> {
                            dateEndSelection = clickedDate
                            if (clickedDate.isBefore(dateStartSelection)) {
                                dateStartSelection = clickedDate
                            }
                        }
                        null -> {}
                    }
                    YearMonth.from(clickedDate).takeIf { it != currentMonth }?.let {
                        currentMonth = it
                    }
                },
                onMonthChanged = { ym -> currentMonth = ym }
            )
        }

        if (whenSelection == 0) {
            var timeSelection by remember { mutableStateOf(LocalTime.now().withSecond(0).withNano(0)) }
            TimeSelectionInput(
                time = timeSelection,
                onTimeChanged = { timeSelection = it },
                onTimeInputClicked = {
                    isCalendarVisible = false
                    activeDateField = null
                },
                keyboardDismissKey = keyboardDismissKey
            )
        }
    }
}

private fun buildMonthWeeks(month: YearMonth, firstDayOfWeek: DayOfWeek): List<List<LocalDate>> {
    val firstOfMonth = month.atDay(1)
    val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val days = (0 until 42).map { start.plusDays(it.toLong()) }
    return days.chunked(7)
}

private fun dayOfWeekOrder(first: DayOfWeek): List<DayOfWeek> {
    val all = DayOfWeek.values().toList()
    val startIndex = all.indexOf(first)
    return all.drop(startIndex) + all.take(startIndex)
}

@Composable
private fun DropdownItem(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = TextStyles.Default.Medium)
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTaskScreenPreview() {
    BGHelpTheme {
        Box(modifier = Modifier.systemBarsPadding()) {
            AddTaskScreen()
        }
    }
}
