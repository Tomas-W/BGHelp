package com.example.bghelp.ui.screens.task.add.date

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.WithRipple
import com.example.bghelp.ui.components.clickableRipple
import com.example.bghelp.ui.components.deselectedTextColor
import com.example.bghelp.ui.components.deselectedTextStyle
import com.example.bghelp.ui.components.selectedTextColor
import com.example.bghelp.ui.components.selectedTextStyle
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.DateField
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.clickableDismissFocus
import java.time.format.DateTimeFormatter
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource

@Composable
fun DateSelection(
    viewModel: AddTaskViewModel
) {
    val locale = LocalConfiguration.current.locales[0]
    val fMonthYearDayShort = remember(locale) {
        DateTimeFormatter.ofPattern("EEE MMM d", locale)
    }
    val dateStartSelection by viewModel.dateStartSelection.collectAsState()
    val dateEndSelection by viewModel.dateEndSelection.collectAsState()
    val activeDateField by viewModel.activeDateField.collectAsState()
    val isEndDateVisible by viewModel.isEndDateVisible.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // startDate
        Text(
            modifier = Modifier
                .widthIn(min = CONST.MIN_WIDTH.dp)
                .clickableDismissFocus {
                    viewModel.toggleCalendarFromDateField(DateField.START)
                },
            text = dateStartSelection.format(fMonthYearDayShort),
            style = if (activeDateField == DateField.START) {
                selectedTextStyle()
            } else {
                deselectedTextStyle()
            },
            color = if (activeDateField == DateField.START) {
                selectedTextColor()
            } else {
                deselectedTextColor()
            }
        )

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        // Double arrow
        WithRipple {
            Box(
                modifier = Modifier
                    .clickableRipple(
                        onClick = {
                            viewModel.toggleEndDateVisible()
                        },
                        radius = Sizes.Icon.S
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(Sizes.Icon.S),
                    painter = painterResource(R.drawable.double_arrow_right),
                    contentDescription = if (isEndDateVisible) stringResource(R.string.task_hide_end_date)
                                         else stringResource(R.string.task_show_end_date)
                )
            }
        }

        Spacer(modifier = Modifier.width(Sizes.Icon.M))

        // endDate
        Text(
            modifier = Modifier
                .alpha(if (isEndDateVisible) 1f else 0f)
                .then(
                    if (isEndDateVisible) {
                        Modifier.clickableDismissFocus {
                            viewModel.toggleCalendarFromDateField(DateField.END)
                        }
                    } else {
                        Modifier
                    }
                ),
            text = dateEndSelection?.format(
                fMonthYearDayShort) ?: dateStartSelection.format(fMonthYearDayShort),
            style = if (activeDateField == DateField.END) {
                selectedTextStyle()
            } else {
                deselectedTextStyle()
            },
            color = if (activeDateField == DateField.END) {
                selectedTextColor()
            } else {
                deselectedTextColor()
            }
        )
    }
}