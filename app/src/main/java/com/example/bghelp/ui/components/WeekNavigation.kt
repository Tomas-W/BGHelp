package com.example.bghelp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.theme.TextStyles
import java.time.LocalDate

@Composable
fun WeekNavigationRow(
    monthYear: String,
    weekNumber: String,
    weekDays: List<LocalDate>,
    selectedDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    //  /      Month 'year     \
    //  \       Week num       /
    //    1  2  3  4  5  6  7

    Column(
        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prev
            Image(
                modifier = Modifier
                    .clickable { onPreviousWeek() },
                painter = painterResource(R.drawable.arrow_left),
                contentScale = ContentScale.Fit,
                contentDescription = "Previous week"
            )

            // Month Year Weeks
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
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

            // Next week
            Image(
                modifier = Modifier
                    .clickable { onNextWeek() },
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "Next week"
            )
        }

        // Day numbers row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekDays.forEach { day ->
                val isToday = day == LocalDate.now()
                val isSelected = day == selectedDate
                val style = when {
                    isToday -> TextStyles.Info.Bold.Large
                    isSelected -> TextStyles.Default.Bold.Large
                    else -> TextStyles.Default.Medium
                }
                Text(
                    modifier = Modifier.clickable { onDaySelected(day) },
                    text = "${day.dayOfMonth}",
                    style = style
                )
            }
        }
    }




}