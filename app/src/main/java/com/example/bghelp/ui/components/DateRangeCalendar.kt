package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun DateRangeCalendar(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    startDate: LocalDate,
    endDate: LocalDate,
    onDayClicked: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    isRangeMode: Boolean = true,
    minDate: LocalDate? = null,
    onDismissRequest: (() -> Unit)? = null
) {
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM") }
    val yearFormatter = remember { DateTimeFormatter.ofPattern("yyyy") }
    val firstDayOfWeek = DayOfWeek.MONDAY
    val weeks = remember(currentMonth, firstDayOfWeek) { buildMonthWeeks(currentMonth, firstDayOfWeek) }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isYearMenuOpen by remember { mutableStateOf(false) }
    val months = remember { Month.entries }
    val years = remember { (AddTaskConstants.MIN_YEAR..AddTaskConstants.MAX_YEAR).toList() }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CalendarDismissRow(onDismissRequest)

        CalendarMonthNavigation(
            currentMonth = currentMonth,
            monthFormatter = monthFormatter,
            yearFormatter = yearFormatter,
            isMonthMenuOpen = isMonthMenuOpen,
            onMonthMenuOpenChange = { isMonthMenuOpen = it },
            isYearMenuOpen = isYearMenuOpen,
            onYearMenuOpenChange = { isYearMenuOpen = it },
            months = months,
            years = years,
            onMonthChanged = onMonthChanged
        )

        Spacer(modifier = Modifier.height(Sizes.Size.S))

        CalendarWeekdayHeader(firstDayOfWeek = firstDayOfWeek)

        CalendarWeeksGrid(
            weeks = weeks,
            currentMonth = currentMonth,
            startDate = startDate,
            endDate = endDate,
            isRangeMode = isRangeMode,
            minDate = minDate,
            onDayClicked = onDayClicked
        )
    }
}

@Composable
private fun CalendarDismissRow(onDismissRequest: (() -> Unit)?) {
    if (onDismissRequest == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismissRequest) {
            Text(
                text = AddTaskStrings.HIDE_CALENDAR,
                style = TextStyles.Grey.Bold.S
            )
        }
    }
}

@Composable
private fun CalendarMonthNavigation(
    currentMonth: YearMonth,
    monthFormatter: DateTimeFormatter,
    yearFormatter: DateTimeFormatter,
    isMonthMenuOpen: Boolean,
    onMonthMenuOpenChange: (Boolean) -> Unit,
    isYearMenuOpen: Boolean,
    onYearMenuOpenChange: (Boolean) -> Unit,
    months: List<Month>,
    years: List<Int>,
    onMonthChanged: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CalendarMonthDropdown(
                currentMonth = currentMonth,
                formatter = monthFormatter,
                isMenuOpen = isMonthMenuOpen,
                onMenuOpenChange = onMonthMenuOpenChange,
                months = months,
                onMonthChanged = onMonthChanged
            )

            Spacer(modifier = Modifier.width(Sizes.Icon.S))

            CalendarYearDropdown(
                currentMonth = currentMonth,
                formatter = yearFormatter,
                isMenuOpen = isYearMenuOpen,
                onMenuOpenChange = onYearMenuOpenChange,
                years = years,
                onMonthChanged = onMonthChanged
            )
        }

        Row {
            CalendarNavigationButton(
                iconRes = R.drawable.navigation_arrow_left,
                contentDescription = AddTaskStrings.PREVIOUS_MONTH
            ) {
                onMonthChanged(currentMonth.minusMonths(1))
            }

            Spacer(modifier = Modifier.width(Sizes.Icon.XXL))

            CalendarNavigationButton(
                iconRes = R.drawable.navigation_arrow_right,
                contentDescription = AddTaskStrings.NEXT_MONTH
            ) {
                onMonthChanged(currentMonth.plusMonths(1))
            }
        }
    }
}

@Composable
private fun CalendarMonthDropdown(
    currentMonth: YearMonth,
    formatter: DateTimeFormatter,
    isMenuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    months: List<Month>,
    onMonthChanged: (YearMonth) -> Unit
) {
    Box {
        Text(
            text = currentMonth.format(formatter),
            style = TextStyles.Grey.Bold.L,
            modifier = Modifier.clickable { onMenuOpenChange(true) }
        )
        CustomDropdown(
            expanded = isMenuOpen,
            onDismissRequest = { onMenuOpenChange(false) },
        ) {
            months.forEach { month ->
                val label = YearMonth.of(currentMonth.year, month).format(formatter)
                DropdownItem(
                    label = label,
                    onClick = {
                        onMonthChanged(YearMonth.of(currentMonth.year, month))
                        onMenuOpenChange(false)
                    },
                    textStyle = TextStyles.Default.M,
                    spacing = Sizes.Icon.L
                )
            }
        }
    }
}

@Composable
private fun CalendarYearDropdown(
    currentMonth: YearMonth,
    formatter: DateTimeFormatter,
    isMenuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    years: List<Int>,
    onMonthChanged: (YearMonth) -> Unit
) {
    Box {
        Text(
            text = currentMonth.format(formatter),
            style = TextStyles.Grey.Bold.L,
            modifier = Modifier.clickable { onMenuOpenChange(true) }
        )
        CustomDropdown(
            expanded = isMenuOpen,
            onDismissRequest = { onMenuOpenChange(false) },
        ) {
            years.forEach { year ->
                val label = YearMonth.of(year, currentMonth.month).format(formatter)
                DropdownItem(
                    label = label,
                    onClick = {
                        onMonthChanged(YearMonth.of(year, currentMonth.month))
                        onMenuOpenChange(false)
                    },
                    textStyle = TextStyles.Default.M,
                    spacing = Sizes.Icon.L
                )
            }
        }
    }
}

@Composable
private fun CalendarNavigationButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    WithRipple {
        Box(
            modifier = Modifier
                .size(Sizes.Icon.XXL)
                .clickableRipple(onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.XL),
                painter = painterResource(iconRes),
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun CalendarWeekdayHeader(firstDayOfWeek: DayOfWeek) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (dow in dayOfWeekOrder(firstDayOfWeek)) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = dow.name.take(1),
                    style = TextStyles.Grey.Bold.M
                )
            }
        }
    }
}

@Composable
private fun CalendarWeeksGrid(
    weeks: List<List<LocalDate>>,
    currentMonth: YearMonth,
    startDate: LocalDate,
    endDate: LocalDate,
    isRangeMode: Boolean,
    minDate: LocalDate?,
    onDayClicked: (LocalDate) -> Unit
) {
    val rangeStart = if (isRangeMode) minOf(startDate, endDate) else startDate
    val rangeEnd = if (isRangeMode) maxOf(startDate, endDate) else startDate

    Column(modifier = Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                week.forEach { day ->
                    val inCurrentMonth = day.month == currentMonth.month
                    val isStart = day == startDate
                    val isEnd = isRangeMode && day == endDate
                    val isBeforeMin = minDate?.let { day.isBefore(it) } ?: false
                    val isSelectable = !isBeforeMin
                    val inRange = isRangeMode && isSelectable && !day.isBefore(rangeStart) && !day.isAfter(rangeEnd)

                    val rangeBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    val startEndBgColor = MaterialTheme.colorScheme.primary
                    val startEndTextColor = MaterialTheme.colorScheme.onPrimary

                    CalendarDayCell(
                        day = day,
                        inCurrentMonth = inCurrentMonth,
                        isStart = isStart,
                        isEnd = isEnd,
                        inRange = inRange,
                        isSelectable = isSelectable,
                        rangeBackgroundColor = rangeBgColor,
                        startEndBackgroundColor = startEndBgColor,
                        startEndTextColor = startEndTextColor,
                        onClick = onDayClicked
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    day: LocalDate,
    inCurrentMonth: Boolean,
    isStart: Boolean,
    isEnd: Boolean,
    inRange: Boolean,
    isSelectable: Boolean,
    rangeBackgroundColor: Color,
    startEndBackgroundColor: Color,
    startEndTextColor: Color,
    onClick: (LocalDate) -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Sizes.Corner.S))
            .background(
                color = if (inRange && !isStart && !isEnd) rangeBackgroundColor else Color.Transparent
            )
            .clickable(
                onClick = { onClick(day) },
                enabled = isSelectable
            ),
        contentAlignment = Alignment.Center
    ) {
        if ((isStart || isEnd) && isSelectable) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(startEndBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    style = TextStyles.Default.Bold.M,
                    color = startEndTextColor,
                )
            }
        } else {
            Text(
                text = day.dayOfMonth.toString(),
                style = TextStyles.Default.M,
                color = when {
                    !isSelectable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    inCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                }
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
    val all = DayOfWeek.entries
    val startIndex = all.indexOf(first)
    return all.drop(startIndex) + all.take(startIndex)
}

