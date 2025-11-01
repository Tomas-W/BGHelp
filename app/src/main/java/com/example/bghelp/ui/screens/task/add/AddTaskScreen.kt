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
import com.example.bghelp.ui.screens.task.add.title.TitleSection
import com.example.bghelp.ui.screens.task.add.date.DateSection
import com.example.bghelp.ui.screens.task.add.remind.RemindSection
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.dismissKeyboardOnTap

object AddTaskConstants {
    const val END_PADDING = 24
    const val MIN_WIDTH = 115

    const val TITLE_MIN_LINES = 1
    const val TITLE_MAX_LINES = 2
    const val INFO_MIN_LINES = 2
    const val INFO_MAX_LINES = 30
    const val MIN_TEXT_LEN = 2
    const val MAX_TEXT_LEN = 40

    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100
    const val REMINDER_START = 0
    const val MIN_REMINDER = 0
    const val MAX_REMINDER = 999
}

enum class UserTitleSelection { TITLE_ONLY, TITLE_AND_INFO }
enum class UserDateSelection { AT_TIME, ALL_DAY }
enum class DateField { START, END }
enum class TimeField { START, END }
enum class TimeSegment { HOUR, MINUTE }
enum class UserRemindSelection { OFF, ON }
enum class RemindType { START, END }
enum class TimeUnit { MINUTES, HOURS, DAYS, WEEKS, MONTHS }
enum class UserSoundSelection { OFF, ONCE, CONTINUOUS }
enum class UserVibrateSelection { OFF, ONCE, CONTINUOUS }

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

            TitleSection(viewModel = viewModel)

            AddTaskDivider()

            DateSection(viewModel = viewModel)

            AddTaskDivider()

            RemindSection(viewModel = viewModel)

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
