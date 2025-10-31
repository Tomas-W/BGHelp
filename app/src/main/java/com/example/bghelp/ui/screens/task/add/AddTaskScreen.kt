package com.example.bghelp.ui.screens.task.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bghelp.ui.components.MainContentContainer
import com.example.bghelp.ui.screens.task.add.date.DateSection
import com.example.bghelp.ui.screens.task.add.notify.NotifySection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.dismissKeyboardOnTap

object AddTaskConstants {
    const val END_PADDING = 24
    const val MIN_WIDTH = 130

    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100
}

enum class UserDateSelection { AT_TIME, ALL_DAY }
enum class DateField { START, END }
enum class TimeField { START, END }
enum class TimeSegment { HOUR, MINUTE }
enum class UserNotifySelection { OFF, ON }
enum class ReminderType { START, END }
enum class TimeUnit { MINUTES, HOURS, DAYS, WEEKS, MONTHS }

data class Reminder(
    val id: Int,
    val value: Int,
    val timeUnit: TimeUnit
)

@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap {
                viewModel.clearAllInputSelections()
            }
    ) {
        MainContentContainer {
            DateSection(viewModel = viewModel)

            AddTaskDivider()

            NotifySection(viewModel = viewModel)
        }
    }
}

@Composable
fun AddTaskSpacerLarge() {
    Spacer(modifier = Modifier.height(Sizes.Size.Small))
}

@Composable
fun AddTaskSpacerMedium() {
    Spacer(modifier = Modifier.height(Sizes.Size.Small))
}

@Composable
fun AddTaskSpacerSmall() {
    Spacer(modifier = Modifier.height(Sizes.Size.ExtraSmall))
}

@Composable
fun AddTaskDivider() {
    AddTaskSpacerMedium()
    HorizontalDivider()
    AddTaskSpacerLarge()
}
