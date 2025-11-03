package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.theme.BGHelpTheme
import com.example.bghelp.ui.theme.TextGrey
import com.example.bghelp.ui.theme.TextStyles
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
private fun ClickableArrow(
    onClick: () -> Unit,
    painterResource: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) = WithRipple {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .clickableRipple(onClick = onClick),
            painter = painterResource(painterResource),
            contentScale = ContentScale.Fit,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(TextGrey)
        )
    }
}

@Composable
fun WeekNavigationRow(
    monthYear: String,
    weekNumber: String,
    weekDays: List<LocalDate>,
    selectedDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDaySelected: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // Prev
        ClickableArrow(
            onClick = onPreviousWeek,
            painterResource = R.drawable.arrow_left,
            contentDescription = "Previous week",
            modifier = Modifier.weight(0.15f)
        )

        Column(modifier = Modifier.weight(0.7f)) {
            // Month Year Weeks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Month year
                Text(
                    text = monthYear,
                    style = TextStyles.Default.Medium
                )
                // Week number
                Text(
                    text = weekNumber,
                    style = TextStyles.Default.Medium
                )
            }

            // Day numbers row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEach { day ->
                    val isToday = day == today
                    val isSelected = day == selectedDate
                    val style = when {
                        isToday -> TextStyles.MainBlue.Bold.Large
                        isSelected -> TextStyles.Default.Bold.Large
                        else -> TextStyles.Default.Medium
                    }
                    Box(
                        modifier = Modifier
                            .clickable { onDaySelected(day) }
                            .height(32.dp) // Fixed height for all days
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${day.dayOfMonth}",
                            style = style
                        )
                    }
                }
            }
        }

        // Next week
        ClickableArrow(
            onClick = onNextWeek,
            painterResource = R.drawable.arrow_right,
            contentDescription = "Next week",
            modifier = Modifier.weight(0.15f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeekNavigationRowPreview() {
    val today = LocalDate.now()
    val weekStart = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }
    
    BGHelpTheme {
        Surface(modifier = Modifier.systemBarsPadding()) {
            WeekNavigationRow(
                monthYear = "January '25",
                weekNumber = "week 5",
                weekDays = weekDays,
                selectedDate = today,
                onPreviousWeek = {},
                onNextWeek = {},
                onDaySelected = {}
            )
        }
    }
}