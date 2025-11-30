package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.bghelp.R
import java.io.Serializable
import java.time.DayOfWeek
import java.time.format.TextStyle

object AddTaskConstants {
    // Misc
    const val START_PADDING = 2 * 22 // Sizes.Icon.Medium
    const val MIN_WIDTH = 115

    // Title
    const val TITLE_MIN_LINES = 1
    const val TITLE_MAX_LINES = 2
    const val INFO_MIN_LINES = 2
    const val INFO_MAX_LINES = 30

    // Date
    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100

    // Repeat
    // Remind
    const val REMINDER_INPUT_WIDTH = 60
    const val REMINDER_START = 0
    const val MIN_REMINDER = 0
    const val MAX_REMINDER = 999
    const val SNOOZE_A_VALUE = 10
    const val SNOOZE_B_VALUE = 1
    const val MIN_SNOOZE = 1
    const val MAX_SNOOZE = 999

    // Image
    const val IMAGE_SIZE = 120
}

interface SectionMeta {
    val headerTextRes: Int
    val iconRes: Int
}

@Composable
fun SectionMeta.getHeaderText(): String {
    return stringResource(headerTextRes)
}

// Title
enum class UserTitleSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_title_only, R.drawable.title_off),
    ON(R.string.task_title_and_info, R.drawable.title_on);

    fun toggle(): UserTitleSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

enum class TitleInputType { TITLE, INFO }

// Date
enum class UserDateSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_at_time, R.drawable.date_off),
    ON(R.string.task_all_day, R.drawable.date_on);

    fun toggle(): UserDateSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

enum class DateField { START, END }
enum class TimeField { START, END }
enum class TimeSegment { HOUR, MINUTE }

// Repeat
enum class UserRepeatSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_dont_repeat, R.drawable.repeat_off),
    WEEKLY(R.string.task_repeat_weekly, R.drawable.repeat_weekly),
    MONTHLY(R.string.task_repeat_monthly, R.drawable.repeat_monthly);

    fun toggle(): UserRepeatSelection = when (this) {
        OFF -> WEEKLY
        WEEKLY -> MONTHLY
        MONTHLY -> OFF
    }
}

enum class RepeatMonthlyDaySelection { ALL, SELECT, LAST }

// Remind
enum class UserRemindSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_dont_remind_me, R.drawable.remind_off),
    ON(R.string.task_remind_me, R.drawable.remind_on);

    fun toggle(): UserRemindSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

data class Reminder(
    val id: Int,
    val value: Int,
    val timeUnit: TimeUnit
)

enum class RemindType { START, END }
enum class TimeUnit { MINUTES, HOURS, DAYS, WEEKS, MONTHS }
data class ActiveReminderInput(val type: RemindType, val id: Int, val snoozeIndex: Int? = null)

@Composable
fun getTimeUnitMap(): Map<TimeUnit, String> {
    val minutes = stringResource(R.string.minutes)
    val hours = stringResource(R.string.hours)
    val days = stringResource(R.string.days)
    val weeks = stringResource(R.string.weeks)
    val months = stringResource(R.string.months)
    return mapOf(
        TimeUnit.MINUTES to minutes,
        TimeUnit.HOURS to hours,
        TimeUnit.DAYS to days,
        TimeUnit.WEEKS to weeks,
        TimeUnit.MONTHS to months
    )
}

@Composable
fun getDayAbbreviations(): Map<Int, String> {
    val locale = java.util.Locale.getDefault()
    return mapOf(
        1 to DayOfWeek.MONDAY.getDisplayName(TextStyle.NARROW, locale),
        2 to DayOfWeek.TUESDAY.getDisplayName(TextStyle.NARROW, locale),
        3 to DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.NARROW, locale),
        4 to DayOfWeek.THURSDAY.getDisplayName(TextStyle.NARROW, locale),
        5 to DayOfWeek.FRIDAY.getDisplayName(TextStyle.NARROW, locale),
        6 to DayOfWeek.SATURDAY.getDisplayName(TextStyle.NARROW, locale),
        7 to DayOfWeek.SUNDAY.getDisplayName(TextStyle.NARROW, locale)
    )
}


// Sound
enum class UserSoundSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_alarm_off, R.drawable.sound_off),
    ONCE(R.string.task_alarm_once, R.drawable.sound_once),
    CONTINUOUS(R.string.task_alarm_continuous, R.drawable.sound_continuous);

    fun toggle(): UserSoundSelection = when (this) {
        OFF -> ONCE
        ONCE -> CONTINUOUS
        CONTINUOUS -> OFF
    }
}

enum class UserNoteSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_note_off, R.drawable.note_off),
    ON(R.string.task_note_on, R.drawable.note_on);

    fun toggle(): UserNoteSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

//Vibrate
enum class UserVibrateSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_vibrate_off, R.drawable.vibrate_off),
    ONCE(R.string.task_vibrate_once, R.drawable.vibrate_once),
    CONTINUOUS(R.string.task_vibrate_continuous, R.drawable.vibrate_continuous);

    fun toggle(): UserVibrateSelection = when (this) {
        OFF -> ONCE
        ONCE -> CONTINUOUS
        CONTINUOUS -> OFF
    }
}

// Location
enum class UserLocationSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_no_location, R.drawable.location_off),
    ON(R.string.task_with_location, R.drawable.location_on);

    fun toggle(): UserLocationSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

data class TaskLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val name: String = ""
) : Serializable

data class TaskImageData(
    val displayName: String,
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
    val source: TaskImageSource
)

enum class TaskImageSource { GALLERY, CAMERA }

// Image
enum class UserImageSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_no_image, R.drawable.image_off),
    ON(R.string.task_with_image, R.drawable.image_on);

    fun toggle(): UserImageSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

// Color
enum class UserColorSelection(
    override val headerTextRes: Int,
    override val iconRes: Int
) : SectionMeta {
    OFF(R.string.task_default_color, R.drawable.color_off),
    ON(R.string.task_custom_color, R.drawable.color_on);

    fun toggle(): UserColorSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

// Save State
sealed interface SaveTaskState {
    data object Idle : SaveTaskState
    data object Saving : SaveTaskState
    data object Success : SaveTaskState
    data class Error(val throwable: Throwable) : SaveTaskState
}

