package com.example.bghelp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.topBorder
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

enum class DateSelectorView {
    COLLAPSED,
    WEEK_NAV,
    CALENDAR
}

@Composable
fun CollapsibleDateSelector(
    modifier: Modifier = Modifier,
    currentYearMonth: YearMonth,
    calendarMonth: YearMonth,
    weekNumber: Int,
    availableWeeks: List<Int>,
    weekDays: List<LocalDate>,
    selectedDate: LocalDate?,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onMonthSelected: (Month) -> Unit,
    onYearSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit,
    onCalendarMonthChanged: (YearMonth) -> Unit,
    onExpansionChanged: ((Boolean, Dp) -> Unit)? = null
) {
    var currentView by rememberSaveable { mutableStateOf(DateSelectorView.WEEK_NAV) }
    var weekNavContentHeightPx by remember { mutableIntStateOf(0) }
    var calendarContentHeightPx by remember { mutableIntStateOf(0) }
    var toggleButtonHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    val isExpanded = currentView != DateSelectorView.COLLAPSED

    LaunchedEffect(currentView, selectedDate) {
        if (currentView == DateSelectorView.CALENDAR && selectedDate != null) {
            val selectedMonth = YearMonth.of(selectedDate.year, selectedDate.month)
            if (calendarMonth != selectedMonth) {
                onCalendarMonthChanged(selectedMonth)
            }
        }
    }

    LaunchedEffect(currentView, weekNavContentHeightPx, calendarContentHeightPx, toggleButtonHeightPx) {
        val contentHeightPx = when (currentView) {
            DateSelectorView.WEEK_NAV -> weekNavContentHeightPx
            DateSelectorView.CALENDAR -> calendarContentHeightPx
            DateSelectorView.COLLAPSED -> 0
        }
        val totalHeight = with(density) { (contentHeightPx + toggleButtonHeightPx).toDp() }
        onExpansionChanged?.invoke(isExpanded, totalHeight)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .topBorder(
                color = if (isExpanded) MaterialTheme.colorScheme.outline else Color.Transparent
            )
    ) {
        ToggleButtonRow(
            currentView = currentView,
            onLeftArrowClick = {
                currentView = when (currentView) {
                    DateSelectorView.COLLAPSED, DateSelectorView.CALENDAR -> {
                        DateSelectorView.WEEK_NAV
                    }

                    DateSelectorView.WEEK_NAV -> {
                        DateSelectorView.COLLAPSED
                    }
                }
            },
            onRightArrowClick = {
                currentView = when (currentView) {
                    DateSelectorView.COLLAPSED, DateSelectorView.WEEK_NAV -> {
                        DateSelectorView.CALENDAR
                    }

                    DateSelectorView.CALENDAR -> {
                        DateSelectorView.COLLAPSED
                    }
                }
            },
            onSizeChanged = { toggleButtonHeightPx = it }
        )

        WeekNavContent(
            isVisible = currentView == DateSelectorView.WEEK_NAV,
            currentYearMonth = currentYearMonth,
            weekNumber = weekNumber,
            availableWeeks = availableWeeks,
            weekDays = weekDays,
            selectedDate = selectedDate,
            onPreviousWeek = onPreviousWeek,
            onNextWeek = onNextWeek,
            onDaySelected = onDaySelected,
            onMonthSelected = onMonthSelected,
            onYearSelected = onYearSelected,
            onWeekSelected = onWeekSelected,
            onSizeChanged = { weekNavContentHeightPx = it }
        )

        CalendarContent(
            isVisible = currentView == DateSelectorView.CALENDAR,
            calendarMonth = calendarMonth,
            selectedDate = selectedDate,
            onDaySelected = onDaySelected,
            onCalendarMonthChanged = onCalendarMonthChanged,
            onCollapse = { currentView = DateSelectorView.COLLAPSED },
            onSizeChanged = { calendarContentHeightPx = it }
        )
    }
}

@Composable
private fun ToggleButtonRow(
    currentView: DateSelectorView,
    onLeftArrowClick: () -> Unit,
    onRightArrowClick: () -> Unit,
    onSizeChanged: (Int) -> Unit
) {
    val leftArrowPointsDown = currentView == DateSelectorView.WEEK_NAV
    val rightArrowPointsDown = currentView == DateSelectorView.CALENDAR
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { onSizeChanged(it.height) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationArrow(
            pointsDown = leftArrowPointsDown,
            onClick = onLeftArrowClick,
            contentDescription = if (leftArrowPointsDown) "Collapse week navigation" else "Show week navigation"
        )
        
        Spacer(modifier = Modifier.width(2 * Sizes.Icon.L))
        
        NavigationArrow(
            pointsDown = rightArrowPointsDown,
            onClick = onRightArrowClick,
            contentDescription = if (rightArrowPointsDown) "Collapse calendar" else "Show calendar"
        )
    }
}

@Composable
private fun NavigationArrow(
    pointsDown: Boolean,
    onClick: () -> Unit,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .width(2 * Sizes.Icon.L)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (pointsDown) R.drawable.navigation_arrow_down else R.drawable.navigation_arrow_up
            ),
            contentDescription = contentDescription,
            modifier = Modifier.size(Sizes.Icon.L),
            tint = MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Composable
private fun WeekNavContent(
    isVisible: Boolean,
    currentYearMonth: YearMonth,
    weekNumber: Int,
    availableWeeks: List<Int>,
    weekDays: List<LocalDate>,
    selectedDate: LocalDate?,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    onMonthSelected: (Month) -> Unit,
    onYearSelected: (Int) -> Unit,
    onWeekSelected: (Int) -> Unit,
    onSizeChanged: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(
            animationSpec = tween(300),
            expandFrom = Alignment.Top
        ),
        exit = shrinkVertically(
            animationSpec = tween(300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { onSizeChanged(it.height) }
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 12.dp)
        ) {
            WeekNavigation(
                currentYearMonth = currentYearMonth,
                weekNumber = weekNumber,
                availableWeeks = availableWeeks,
                weekDays = weekDays,
                selectedDate = selectedDate,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                onDaySelected = onDaySelected,
                onMonthSelected = onMonthSelected,
                onYearSelected = onYearSelected,
                onWeekSelected = onWeekSelected
            )
        }
    }
}

@Composable
private fun CalendarContent(
    isVisible: Boolean,
    calendarMonth: YearMonth,
    selectedDate: LocalDate?,
    onDaySelected: (LocalDate) -> Unit,
    onCalendarMonthChanged: (YearMonth) -> Unit,
    onCollapse: () -> Unit,
    onSizeChanged: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(
            animationSpec = tween(300),
            expandFrom = Alignment.Top
        ),
        exit = shrinkVertically(
            animationSpec = tween(300),
            shrinkTowards = Alignment.Top
        )
    ) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 12.dp)
                .onSizeChanged { onSizeChanged(it.height) }
        ) {
            DateRangeCalendar(
                currentMonth = calendarMonth,
                startDate = selectedDate ?: LocalDate.now(),
                endDate = selectedDate ?: LocalDate.now(),
                onDayClicked = onDaySelected,
                onMonthChanged = onCalendarMonthChanged,
                isRangeMode = false,
                minDate = null,
                onDismissRequest = onCollapse,
                onInvalidDateClicked = null,
                selectedDate = selectedDate,
                showDismissButton = false
            )
        }
    }
}
