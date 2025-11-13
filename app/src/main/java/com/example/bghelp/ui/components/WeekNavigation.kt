package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.BGHelpTheme
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles
import java.time.DayOfWeek
import java.time.LocalDate

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
    val colorScheme = MaterialTheme.colorScheme
    val selectedDayColor = colorScheme.secondary
    val todayDayColor = colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Sizes.Icon.XL,
                vertical = 12.dp
            ),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month year
            Text(
                text = monthYear,
                style = TextStyles.Grey.M
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Sizes.Icon.M),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev arrow
                WithRipple {
                    Icon(
                        modifier = Modifier
                            .size(Sizes.Icon.M)
                            .clickableRipple( onPreviousWeek ),
                        painter = painterResource(R.drawable.navigation_arrow_left),
                        contentDescription = "Previous week"
                    )
                }

                // Week number
                Text(
                    text = weekNumber,
                    style = TextStyles.Grey.XS
                )

                // Next arrow
                WithRipple {
                    Icon(
                        modifier = Modifier
                            .size(Sizes.Icon.M)
                            .clickableRipple( onNextWeek ),
                        painter = painterResource(R.drawable.navigation_arrow_right),
                        contentDescription = "Next week"
                    )
                }
            }
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
                val isSelected = day == selectedDate
                // Set selected color
                val dayModifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(Sizes.Corner.S))
                    .background(
                        color = if (isSelected) selectedDayColor else Color.Transparent
                    )
                    .clickableRipple(onClick = { onDaySelected(day) })

                Box(
                    modifier = dayModifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (isToday) {
                        Box(
                            // Set today color
                            modifier = Modifier
                                .size(Sizes.Icon.XL)
                                .clip(CircleShape)
                                .background(todayDayColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${day.dayOfMonth}",
                                style = TextStyles.White.Bold.M,)
                        }
                    } else {
                        Text(
                            text = "${day.dayOfMonth}",
                            style = if (isSelected) TextStyles.Default.Bold.M else TextStyles.Default.M
                        )
                    }
                }
            }

        }
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