package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.constants.UiConstants as UI
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableWithCalendarRipple
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskSpacerSmall
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
    isRangeMode: Boolean = true
) = WithRipple {
    val fMonthLong = remember { DateTimeFormatter.ofPattern("MMMM") }
    val fYearLong = remember { DateTimeFormatter.ofPattern("yyyy") }
    val firstDayOfWeek = DayOfWeek.MONDAY
    val weeks = remember(currentMonth, firstDayOfWeek) { buildMonthWeeks(currentMonth, firstDayOfWeek) }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isYearMenuOpen by remember { mutableStateOf(false) }
    val months = remember { Month.entries }
    val years = remember { (AddTaskConstants.MIN_YEAR..AddTaskConstants.MAX_YEAR).toList() }
    val dropdownHeight = (6 * UI.DROPDOWN_ITEM_HEIGHT).dp

    Column(modifier = modifier.fillMaxWidth()) {
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
                // Month label
                Box {
                    Text(
                        text = currentMonth.format(fMonthLong),
                        style = TextStyles.Grey.Bold.Large,
                        modifier = Modifier.clickable { isMonthMenuOpen = true }
                    )

                    // Month dropdown
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
                                },
                                textStyle = TextStyles.Default.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(Sizes.Size.Medium))

                Box {
                    // Year label
                    Text(
                        text = currentMonth.format(fYearLong),
                        style = TextStyles.Grey.Bold.Large,
                        modifier = Modifier.clickable { isYearMenuOpen = true }
                    )

                    // Year dropdown
                    DropdownMenu(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary)
                            .heightIn(max = dropdownHeight),
                        expanded = isYearMenuOpen,
                        onDismissRequest = { isYearMenuOpen = false },
                        offset = DpOffset(x = 0.dp, y = 4.dp)
                    ) {
                        years.forEach { y ->
                            val label = YearMonth.of(y, currentMonth.month).format(fYearLong)
                            DropdownItem(
                                label = label,
                                onClick = {
                                    onMonthChanged(YearMonth.of(y, currentMonth.month))
                                    isYearMenuOpen = false
                                },
                                textStyle = TextStyles.Default.Medium
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

        AddTaskSpacerSmall()

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

        Spacer(modifier = Modifier.height(Sizes.Size.ExtraSmall))

        val rangeStart = if (isRangeMode) minOf(startDate, endDate) else startDate
        val rangeEnd = if (isRangeMode) maxOf(startDate, endDate) else startDate

        // Weeks grid
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
                        val isEnd = isRangeMode && day == endDate
                        val inRange = isRangeMode && !day.isBefore(rangeStart) && !day.isAfter(rangeEnd)

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
                                    // Highlight if in selection range
                                    color = if (inRange && !isStart && !isEnd) rangeBgColor else Color.Transparent
                                )
                                .clickableWithCalendarRipple(onClick = { onDayClicked(day) }),
                            contentAlignment = Alignment.Center
                        ) {
                            // Start & End highlight
                            if (isStart || isEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
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

