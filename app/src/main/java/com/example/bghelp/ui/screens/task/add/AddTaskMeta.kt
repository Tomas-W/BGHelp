package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.net.Uri
import com.example.bghelp.R
import com.example.bghelp.ui.theme.TextStyles
import java.io.Serializable

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
    // Image
    const val IMAGE_SIZE = 120
}

object AddTaskStrings {
    // Title
    const val TITLE_HINT = "Title"
    const val INFO_HINT = "Info"
    const val TITLE_ONLY = "Title only"
    const val TITLE_AND_INFO = "Title and info"
    
    // Date
    const val DATE_CANNOT_BE_IN_PAST = "Date cannot be in the past"
    const val AT_TIME = "At time"
    const val ALL_DAY = "All day"
    
    // Repeat
    const val DONT_REPEAT = "Don't repeat"
    const val REPEAT_WEEKLY = "Repeat weekly"
    const val REPEAT_MONTHLY = "Repeat monthly"
    const val EVERY = "Every"
    const val REPEAT_WEEKS = "weeks"
    const val ALL_DAYS = "All days"
    const val SELECT_DAYS = "Select days"
    const val LAST_OF_MONTH = "Last of month"
    
    // Remind
    const val BEFORE_START = "Before start"
    const val BEFORE_END = "Before end"
    const val DONT_REMIND_ME = "Don't remind me"
    const val REMIND_ME = "Remind me"
    const val ADD_REMINDER = "Add reminder"
    const val REMOVE_REMINDER = "Remove reminder"
    
    // Sound
    const val SELECT_ALARM = "Select alarm"
    const val CREATE_ALARM = "Create alarm +"
    const val ALARM_OFF = "Alarm off"
    const val ALARM_ONCE = "Alarm once"
    const val ALARM_CONTINUOUS = "Alarm continuous"
    
    // Vibrate
    const val VIBRATE_OFF = "Vibrate off"
    const val VIBRATE_ONCE = "Vibrate once"
    const val VIBRATE_CONTINUOUS = "Vibrate continuous"
    
    // Note
    const val SELECT_NOTE = "Select note"
    const val CREATE_NOTE = "Create note +"
    const val NOTE_OFF = "Note off"
    const val NOTE_ON = "Note on"
    
    // Image
    const val ADD_IMAGE = "Add image"
    const val NO_IMAGE_SELECTED = "No image selected"
    const val IMAGE_FROM_LIBRARY = "Select from library"
    const val IMAGE_FROM_CAMERA = "Capture photo"
    const val CAPTURED_IMAGE = "Captured image"
    const val CAMERA_PERMISSION_REQUIRED = "Camera permission required to capture photo"
    const val NO_IMAGE = "No image"
    const val WITH_IMAGE = "With image"
    
    // Color
    const val SELECT_COLOR = "Select color"
    const val DEFAULT = "Default"
    const val RED = "Red"
    const val GREEN = "Green"
    const val YELLOW = "Yellow"
    const val CYAN = "Cyan"
    const val MAGENTA = "Magenta"
    const val DEFAULT_COLOR = "Default color"
    const val CUSTOM_COLOR = "Custom color"
    
    // Location
    const val ADD_LOCATION = "Add location"
    const val REMOVE_LOCATION = "Remove location"
    const val UNNAMED_LOCATION = "unnamed"
    const val NO_LOCATION = "No location"
    const val WITH_LOCATION = "With Location"
    
    // Misc
    const val MINUTES = "Minutes"
    const val HOURS = "Hours"
    const val DAYS = "Days"
    const val WEEKS = "Weeks"
    const val MONTHS = "Months"
    const val SELECT_ALL = "Select all"
    const val DESELECT_ALL = "Deselect all"
    
    // Content description
    const val SHOW_END_DATE = "Show end date"
    const val HIDE_END_DATE = "Hide end date"
    const val SHOW_END_TIME = "Show end time"
    const val HIDE_END_TIME = "Hide end time"
    const val PREVIOUS_MONTH = "Previous month"
    const val NEXT_MONTH = "Next month"
    const val HIDE_CALENDAR = "Hide calendar"
    const val CLEAR_FORM = "Clear form"
    
    // Actions
    const val CANCEL = "Cancel"
    const val SAVING = "Saving..."
    const val SAVE_TASK = "Save Task"
    const val RESET_ALL = "Reset all"
    
    // Validation errors
    const val VALIDATION_TITLE_EMPTY = "Title cannot be empty"
    const val VALIDATION_START_TIME_PAST = "Start time cannot be in the past"
    const val VALIDATION_END_TIME_BEFORE_START = "End time must be after start time"
    const val VALIDATION_REPEAT_MIN_DAYS = "Repeats require a 1 day minimum"
    const val VALIDATION_REMINDERS_PAST = "Reminders cannot be in the past"
    const val ERROR_SAVE_FAILED = "Failed to save task. Please try again."
    const val ERROR_ADDING_IMAGE = "Error adding image"
}

val selectedStyle = TextStyles.Default.Bold.M
val deselectedStyle = TextStyles.Default.M
val highlightedStyle = TextStyles.Main.Bold.M


interface SectionMeta {
    val headerText: String
    val iconRes: Int
}

// Title
enum class UserTitleSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.TITLE_ONLY, R.drawable.title_off),
    ON(AddTaskStrings.TITLE_AND_INFO, R.drawable.title_on);

    fun toggle(): UserTitleSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}
enum class TitleInputType { TITLE, INFO }

// Date
enum class UserDateSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.AT_TIME, R.drawable.date_off),
    ON(AddTaskStrings.ALL_DAY, R.drawable.date_on);

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
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.DONT_REPEAT, R.drawable.repeat_off),
    WEEKLY(AddTaskStrings.REPEAT_WEEKLY, R.drawable.repeat_weekly),
    MONTHLY(AddTaskStrings.REPEAT_MONTHLY, R.drawable.repeat_monthly);

    fun toggle(): UserRepeatSelection = when (this) {
        OFF -> WEEKLY
        WEEKLY -> MONTHLY
        MONTHLY -> OFF
    }
}
enum class RepeatMonthlyDaySelection { ALL, SELECT, LAST }

// Remind
enum class UserRemindSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.DONT_REMIND_ME, R.drawable.remind_off),
    ON(AddTaskStrings.REMIND_ME, R.drawable.remind_on);

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
data class ActiveReminderInput(val type: RemindType, val id: Int)
// Sound
enum class UserSoundSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.ALARM_OFF, R.drawable.sound_off),
    ONCE(AddTaskStrings.ALARM_ONCE, R.drawable.sound_once),
    CONTINUOUS(AddTaskStrings.ALARM_CONTINUOUS, R.drawable.sound_continuous);

    fun toggle(): UserSoundSelection = when (this) {
        OFF -> ONCE
        ONCE -> CONTINUOUS
        CONTINUOUS -> OFF
    }
}
enum class UserNoteSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.NOTE_OFF, R.drawable.note_off),
    ON(AddTaskStrings.NOTE_ON, R.drawable.note_on);

    fun toggle(): UserNoteSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}
//Vibrate
enum class UserVibrateSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.VIBRATE_OFF, R.drawable.vibrate_off),
    ONCE(AddTaskStrings.VIBRATE_ONCE, R.drawable.vibrate_once),
    CONTINUOUS(AddTaskStrings.VIBRATE_CONTINUOUS, R.drawable.vibrate_continuous);

    fun toggle(): UserVibrateSelection = when (this) {
        OFF -> ONCE
        ONCE -> CONTINUOUS
        CONTINUOUS -> OFF
    }
}

// Location
enum class UserLocationSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.NO_LOCATION, R.drawable.location_off),
    ON(AddTaskStrings.WITH_LOCATION, R.drawable.location_on);

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
): Serializable

data class TaskImageData(
    val displayName: String,
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
    val source: TaskImageSource
)

enum class TaskImageSource { GALLERY, CAMERA }

// Image
enum class UserImageSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.NO_IMAGE, R.drawable.image_off),
    ON(AddTaskStrings.WITH_IMAGE, R.drawable.image_on);

    fun toggle(): UserImageSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}

// Color
enum class UserColorSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF(AddTaskStrings.DEFAULT_COLOR, R.drawable.color_off),
    ON(AddTaskStrings.CUSTOM_COLOR, R.drawable.color_on);

    fun toggle(): UserColorSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}
enum class UserColorChoices { DEFAULT, RED, GREEN, YELLOW, CYAN, MAGENTA }

// Save State
sealed interface SaveTaskState {
    data object Idle : SaveTaskState
    data object Saving : SaveTaskState
    data object Success : SaveTaskState
    data class Error(val throwable: Throwable) : SaveTaskState
}

