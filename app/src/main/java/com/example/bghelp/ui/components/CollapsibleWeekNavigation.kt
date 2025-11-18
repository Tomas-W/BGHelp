package com.example.bghelp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.bottomBorder
import com.example.bghelp.ui.utils.topBorder
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

@Composable
fun CollapsibleWeekNavigation(
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
    onExpansionChanged: ((Boolean, Dp) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(true) }
    var weekNavContentHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // When collapsed, move down by content height (so only arrow shows)
    val offsetY by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else with(density) { weekNavContentHeightPx.toDp() },
        animationSpec = tween(300),
        label = "WeekNav Offset"
    )

    // Report expansion state and height changes
    LaunchedEffect(isExpanded, weekNavContentHeightPx) {
        val heightDp = with(density) { weekNavContentHeightPx.toDp() }
        onExpansionChanged?.invoke(isExpanded, heightDp)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Column that slides up/down
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomStart)
                .offset(y = offsetY)
                .background(color = MaterialTheme.colorScheme.background)
                .topBorder(
                    strokeWidth = 2.dp,
                    color = if (isExpanded) MaterialTheme.colorScheme.outline else Color.Transparent
                )
                .bottomBorder(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
        ) {
            // Arrow to toggle expansion
            WithRipple {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .bottomBorder(
                            strokeWidth = 2.dp,
                            color = if (!isExpanded) MaterialTheme.colorScheme.outline else Color.Transparent
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3 * Sizes.Icon.L)
                            .height(Sizes.Icon.L)
                            .clickableRipple(onClick = { isExpanded = !isExpanded }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isExpanded) R.drawable.navigation_arrow_down else R.drawable.navigation_arrow_up
                            ),
                            contentDescription = if (isExpanded) "Hide week navigation" else "Show week navigation",
                            modifier = Modifier.size(Sizes.Icon.L),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.onSizeChanged { weekNavContentHeightPx = it.height }
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
}


