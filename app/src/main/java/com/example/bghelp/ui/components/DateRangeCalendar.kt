package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextBold
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.theme.xsTextBold
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration

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
    onDismissRequest: (() -> Unit)? = null,
    onInvalidDateClicked: ((LocalDate) -> Unit)? = null,
    selectedDate: LocalDate? = null,
    showDismissButton: Boolean = true
) {
    val locale = LocalConfiguration.current.locales[0]
    val monthFormatter = remember(locale) { DateTimeFormatter.ofPattern("MMMM", locale) }
    val yearFormatter = remember(locale) { DateTimeFormatter.ofPattern("yyyy", locale) }
    val firstDayOfWeek = DayOfWeek.MONDAY
    val weeks =
        remember(currentMonth, firstDayOfWeek) { buildMonthWeeks(currentMonth, firstDayOfWeek) }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isYearMenuOpen by remember { mutableStateOf(false) }
    val months = remember { Month.entries }
    val years = remember { (AddTaskConstants.MIN_YEAR..AddTaskConstants.MAX_YEAR).toList() }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CalendarDismissRow(onDismissRequest, showDismissButton)

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

        CalendarWeekdayHeader(firstDayOfWeek = firstDayOfWeek, locale = locale)

        CalendarWeeksGrid(
            weeks = weeks,
            currentMonth = currentMonth,
            startDate = startDate,
            endDate = endDate,
            isRangeMode = isRangeMode,
            minDate = minDate,
            onDayClicked = onDayClicked,
            onInvalidDateClicked = onInvalidDateClicked,
            selectedDate = selectedDate
        )
    }
}

@Composable
private fun CalendarDismissRow(onDismissRequest: (() -> Unit)?, showDismissButton: Boolean = true) {
    if (onDismissRequest == null || !showDismissButton) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismissRequest) {
            Text(
                text = AddTaskStrings.HIDE_CALENDAR,
                style = MaterialTheme.typography.xsTextBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var dropdownWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val horizontalOffset = remember(anchorWidthPx, dropdownWidthPx, density) {
        if (anchorWidthPx == 0 || dropdownWidthPx == 0) {
            DpOffset.Zero
        } else {
            val diffPx = (anchorWidthPx - dropdownWidthPx) / 2f
            DpOffset(with(density) { diffPx.toDp() }, 0.dp)
        }
    }

    Box {
        Text(
            text = currentMonth.format(formatter),
            style = MaterialTheme.typography.lTextBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .onGloballyPositioned { anchorWidthPx = it.size.width }
                .clickable { onMenuOpenChange(true) }
        )
        CustomDropdown(
            expanded = isMenuOpen,
            onDismissRequest = { onMenuOpenChange(false) },
            offset = horizontalOffset,
            modifier = Modifier.onSizeChanged { dropdownWidthPx = it.width }
        ) {
            months.forEach { month ->
                val label = YearMonth.of(currentMonth.year, month).format(formatter)
                DropdownItem(
                    label = label,
                    onClick = {
                        onMonthChanged(YearMonth.of(currentMonth.year, month))
                        onMenuOpenChange(false)
                    },
                    textStyle = if (month == currentMonth.month) {
                        selectedDropdownStyle()
                    } else {
                        deselectedDropdownStyle()
                    },
                    spacing = Sizes.Icon.M
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
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var dropdownWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val horizontalOffset = remember(anchorWidthPx, dropdownWidthPx, density) {
        if (anchorWidthPx == 0 || dropdownWidthPx == 0) {
            DpOffset.Zero
        } else {
            val diffPx = (anchorWidthPx - dropdownWidthPx) / 2f
            DpOffset(with(density) { diffPx.toDp() }, 0.dp)
        }
    }

    Box {
        Text(
            text = currentMonth.format(formatter),
            style = MaterialTheme.typography.lTextBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .onGloballyPositioned { anchorWidthPx = it.size.width }
                .clickable { onMenuOpenChange(true) }
        )
        CustomDropdown(
            expanded = isMenuOpen,
            onDismissRequest = { onMenuOpenChange(false) },
            offset = horizontalOffset,
            modifier = Modifier.onSizeChanged { dropdownWidthPx = it.width }
        ) {
            years.forEach { year ->
                val label = YearMonth.of(year, currentMonth.month).format(formatter)
                DropdownItem(
                    label = label,
                    onClick = {
                        onMonthChanged(YearMonth.of(year, currentMonth.month))
                        onMenuOpenChange(false)
                    },
                    textStyle = if (year == currentMonth.year) {
                        selectedDropdownStyle()
                    } else {
                        deselectedDropdownStyle()
                    },
                    spacing = Sizes.Icon.M
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
private fun CalendarWeekdayHeader(firstDayOfWeek: DayOfWeek, locale: Locale) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (dow in dayOfWeekOrder(firstDayOfWeek)) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = dow.getDisplayName(TextStyle.NARROW, locale),
                    style = MaterialTheme.typography.lTextDefault,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    onDayClicked: (LocalDate) -> Unit,
    onInvalidDateClicked: ((LocalDate) -> Unit)? = null,
    selectedDate: LocalDate? = null
) {
    val rangeStart = if (isRangeMode) minOf(startDate, endDate) else startDate
    val rangeEnd = if (isRangeMode) maxOf(startDate, endDate) else startDate

    Column(modifier = Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { day ->
                    val inCurrentMonth = day.month == currentMonth.month
                    val isStart = isRangeMode && day == startDate
                    val isEnd = isRangeMode && day == endDate
                    val isBeforeMin = minDate?.let { day.isBefore(it) } ?: false
                    val isSelectable = !isBeforeMin
                    val inRange =
                                isRangeMode
                                && isSelectable
                                && !day.isBefore(rangeStart) &&
                                !day.isAfter(rangeEnd)

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
                        isRangeMode = isRangeMode,
                        rangeBackgroundColor = rangeBgColor,
                        startEndBackgroundColor = startEndBgColor,
                        startEndTextColor = startEndTextColor,
                        onClick = onDayClicked,
                        onInvalidClick = onInvalidDateClicked,
                        selectedDate = selectedDate
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
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
    isRangeMode: Boolean,
    rangeBackgroundColor: Color,
    startEndBackgroundColor: Color,
    startEndTextColor: Color,
    onClick: (LocalDate) -> Unit,
    onInvalidClick: ((LocalDate) -> Unit)? = null,
    selectedDate: LocalDate? = null
) {
    val today = remember { LocalDate.now() }
    val isToday = day == today
    val isSelected = selectedDate?.let { it == day } == true

    val dayModifier = Modifier
        .weight(1f)
        .aspectRatio(1f)
        .clip(RoundedCornerShape(Sizes.Corner.S))
        .background(
            color = when {
                inRange && !isStart && !isEnd -> rangeBackgroundColor
                else -> Color.Transparent
            }
        )
        .clickable(
            onClick = {
                if (isSelectable) {
                    onClick(day)
                } else {
                    onInvalidClick?.invoke(day)
                }
            }
        )

    Box(
        modifier = dayModifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            // Range mode: show start/end dates with primary color
            (isStart || isEnd) && isSelectable && isRangeMode -> {
                Box(
                    modifier = Modifier
                        .size(Sizes.Icon.XXXL)
                        .clip(CircleShape)
                        .background(startEndBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = MaterialTheme.typography.lTextBold,
                        color = startEndTextColor,
                    )
                }
            }
            // Selected (single date mode): show primary color circle with onPrimary text
            isSelected -> {
                Box(
                    modifier = Modifier
                        .size(Sizes.Icon.XXXL)
                        .clip(CircleShape)
                        .background(startEndBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = MaterialTheme.typography.lTextBold,
                        color = startEndTextColor
                    )
                }
                // Today border on top if also today
                if (isToday) {
                    Box(
                        modifier = Modifier
                            .size(Sizes.Icon.XXXL)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    )
                }
            }
            // Today: show bold text with black border circle
            isToday -> {
                Box(
                    modifier = Modifier
                        .size(Sizes.Icon.XXXL)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        style = MaterialTheme.typography.lTextBold
                    )
                }
            }
            // Default: normal text
            else -> {
                Text(
                    text = day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.lTextDefault,
                    color = when {
                        !isSelectable -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        inCurrentMonth -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }
                )
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

