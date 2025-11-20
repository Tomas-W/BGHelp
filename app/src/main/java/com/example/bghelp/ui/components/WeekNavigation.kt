package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.AddTaskConstants
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun WeekNavigation(
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
    onWeekSelected: (Int) -> Unit
) {
    val today = remember { LocalDate.now() }
    val monthFormatter = remember { DateTimeFormatter.ofPattern("MMMM") }
    val yearFormatter = remember { DateTimeFormatter.ofPattern("yyyy") }
    var isMonthMenuOpen by remember { mutableStateOf(false) }
    var isYearMenuOpen by remember { mutableStateOf(false) }
    val months = remember { Month.entries }
    val years = remember { (AddTaskConstants.MIN_YEAR..AddTaskConstants.MAX_YEAR).toList() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Month dropdown
                WeekNavigationMonthDropdown(
                    currentYearMonth = currentYearMonth,
                    formatter = monthFormatter,
                    isMenuOpen = isMonthMenuOpen,
                    onMenuOpenChange = { isMonthMenuOpen = it },
                    months = months,
                    onMonthSelected = onMonthSelected
                )

                Spacer(modifier = Modifier.width(Sizes.Icon.S))

                // Year dropdown
                WeekNavigationYearDropdown(
                    currentYearMonth = currentYearMonth,
                    formatter = yearFormatter,
                    isMenuOpen = isYearMenuOpen,
                    onMenuOpenChange = { isYearMenuOpen = it },
                    years = years,
                    onYearSelected = onYearSelected
                )
            }

            // Week navigation
            NavigationRow(
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                weekNumber = weekNumber,
                availableWeeks = availableWeeks,
                onWeekSelected = onWeekSelected
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Week days
            weekDays.forEach { day ->
                val isToday = day == today
                val isSelected = selectedDate?.let { it == day } == true
                val selectedDayColor = MaterialTheme.colorScheme.primary
                val selectedDayTextColor = MaterialTheme.colorScheme.onPrimary
                
                val dayModifier = Modifier
                    .size(Sizes.Icon.XXXL)
                    .let { baseModifier ->
                        if (isToday) {
                            baseModifier.clip(CircleShape).border(2.dp, Color.Black, CircleShape)
                        } else {
                            baseModifier
                        }
                    }
                    .clickableRipple(onClick = { onDaySelected(day) })

                Box(
                    modifier = dayModifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(Sizes.Icon.XXXL)
                                .clip(CircleShape)
                                .background(selectedDayColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${day.dayOfMonth}",
                                style = TextStyles.Default.Bold.M,
                                color = selectedDayTextColor
                            )
                        }
                    } else if (isToday) {
                        Text(
                            text = "${day.dayOfMonth}",
                            style = TextStyles.Default.Bold.M
                        )
                    } else {
                        Text(
                            text = "${day.dayOfMonth}",
                            style = TextStyles.Default.M
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun NavigationRow(
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    weekNumber: Int,
    availableWeeks: List<Int>,
    onWeekSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Sizes.Icon.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev week
        NavigationArrow(
            onClick = onPreviousWeek,
            iconRes = R.drawable.navigation_arrow_left,
            iconDescription = "Previous week"
        )

        // Week dropdown
        WeekNavigationWeekDropdown(
            weekNumber = weekNumber,
            availableWeeks = availableWeeks,
            onWeekSelected = onWeekSelected
        )

        // Next week
        NavigationArrow(
            onClick = onNextWeek,
            iconRes = R.drawable.navigation_arrow_right,
            iconDescription = "Next week"
        )
    }
}

@Composable
fun NavigationArrow(
    onClick: () -> Unit,
    iconRes: Int,
    iconDescription: String
) {
    WithRipple {
        Box(
            modifier = Modifier
                .size(Sizes.Icon.XL)
                .clickableRipple( onClick ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(Sizes.Icon.XL),
                painter = painterResource(iconRes),
                contentDescription = iconDescription
            )
        }
    }
}

@Composable
private fun WeekNavigationWeekDropdown(
    weekNumber: Int,
    availableWeeks: List<Int>,
    onWeekSelected: (Int) -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val formattedWeek = remember(weekNumber) { weekNumber.toString().padStart(2, '0') }
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var dropdownWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // To center dropdown
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
            text = formattedWeek,
            style = TextStyles.Grey.Bold.M,
            modifier = Modifier
                .onGloballyPositioned { anchorWidthPx = it.size.width }
                .clickable { isMenuOpen = true }
        )
        CustomDropdown(
            expanded = isMenuOpen,
            onDismissRequest = { isMenuOpen = false },
            offset = horizontalOffset,
            modifier = Modifier.onSizeChanged { dropdownWidthPx = it.width }
        ) {
            availableWeeks.forEach { week ->
                val itemWeek = week.toString().padStart(2, '0')
                val isSelected = week == weekNumber
                DropdownItem(
                    label = itemWeek,
                    onClick = {
                        onWeekSelected(week)
                        isMenuOpen = false
                    },
                    textStyle = if (isSelected) TextStyles.Default.Bold.S else TextStyles.Default.M,
                    spacing = Sizes.Icon.XS
                )
            }
        }
    }
}

@Composable
private fun WeekNavigationMonthDropdown(
    currentYearMonth: YearMonth,
    formatter: DateTimeFormatter,
    isMenuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    months: List<Month>,
    onMonthSelected: (Month) -> Unit
) {
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var dropdownWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // To center dropdown
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
            text = currentYearMonth.format(formatter),
            style = TextStyles.Grey.Bold.M,
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
                val label = YearMonth.of(currentYearMonth.year, month).format(formatter)
                val isSelected = month == currentYearMonth.month
                DropdownItem(
                    label = label,
                    onClick = {
                        onMonthSelected(month)
                        onMenuOpenChange(false)
                    },
                    textStyle = if (isSelected) TextStyles.Default.Bold.M else TextStyles.Default.M,
                    spacing = Sizes.Icon.S
                )
            }
        }
    }
}

@Composable
private fun WeekNavigationYearDropdown(
    currentYearMonth: YearMonth,
    formatter: DateTimeFormatter,
    isMenuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    years: List<Int>,
    onYearSelected: (Int) -> Unit
) {
    var anchorWidthPx by remember { mutableIntStateOf(0) }
    var dropdownWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    // To center dropdown
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
            text = currentYearMonth.format(formatter),
            style = TextStyles.Grey.Bold.M,
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
                val label = YearMonth.of(year, currentYearMonth.month).format(formatter)
                val isSelected = year == currentYearMonth.year
                DropdownItem(
                    label = label,
                    onClick = {
                        onYearSelected(year)
                        onMenuOpenChange(false)
                    },
                    textStyle = if (isSelected) TextStyles.Default.Bold.M else TextStyles.Default.M,
                    spacing = Sizes.Icon.S
                )
            }
        }
    }
}
