package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.net.Uri
import com.example.bghelp.R
import com.example.bghelp.ui.theme.TextStyles
import java.io.Serializable

object AddTaskConstants {
    // Misc
    const val START_PADDING = 2 * 22 // Sizes.Icon.Medium
    const val END_PADDING = 2 * 22 // Sizes.Icon.Medium
    const val MIN_WIDTH = 115
    const val DROPDOWN_ITEMS = 6
    // Title
    const val TITLE_MIN_LINES = 1
    const val TITLE_MAX_LINES = 2
    const val INFO_MIN_LINES = 2
    const val INFO_MAX_LINES = 30
    // When
    const val MIN_YEAR = 2025
    const val MAX_YEAR = 2100
    // Repeat
    const val REPEAT_LABEL_WIDTH = 60
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
    // When
    // Remind
    const val BEFORE_START = "Before start"
    const val BEFORE_END = "Before end"
    const val SELECT_ALL = "Select all"
    const val DESELECT_ALL = "Deselect all"
    // sound
    const val SELECT_ALARM = "Select alarm"
    const val CREATE_ALARM = "Create alarm +"
    // vibrate
    // Note
    const val SELECT_NOTE = "Select note"
    const val CREATE_NOTE = "Create note +"
    // Image
    const val ADD_IMAGE = "Add image"
    const val NO_IMAGE_SELECTED = "No image selected"
    const val IMAGE_FROM_LIBRARY = "Select from library"
    const val IMAGE_FROM_CAMERA = "Capture photo"
    const val CAPTURED_IMAGE = "Captured image"
    const val CAMERA_PERMISSION_REQUIRED = "Camera permission required to capture photo"
    // Color
    const val SELECT_COLOR = "Select color"
    const val DEFAULT = "Default"
    const val RED = "Red"
    const val GREEN = "Green"
    const val YELLOW = "Yellow"
    const val CYAN = "Cyan"
    const val MAGENTA = "Magenta"
    // Misc
    const val MINUTES = "Minutes"
    const val HOURS = "Hours"
    const val DAYS = "Days"
    const val WEEKS = "Weeks"
    const val MONTHS = "Months"
    // Content description
    const val SHOW_END_DATE = "Show end date"
    const val HIDE_END_DATE = "Hide end date"
    const val SHOW_END_TIME = "Show end time"
    const val HIDE_END_TIME = "Hide end time"

    const val PREVIOUS_MONTH = "Previous month"
    const val NEXT_MONTH = "Next month"

    const val ADD_REMINDER = "Add reminder"
    const val REMOVE_REMINDER = "Remove reminder"

    // Location
    const val ADD_LOCATION = "Add location"
    const val LOCATION_PLACEHOLDER = "No location selected"
    const val LOCATION_LAT_LABEL = "Latitude"
    const val LOCATION_LONG_LABEL = "Longitude"
    const val LOCATION_ADDRESS_LABEL = "Address"
}

val selectedStyle = TextStyles.Default.Bold.Medium
val deselectedStyle = TextStyles.Default.Medium

interface SectionMeta {
    val headerText: String
    val iconRes: Int
}

// Title
enum class UserTitleSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF("Title only", R.drawable.title_off),
    ON("Title and info", R.drawable.title_on);

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
    OFF("At time", R.drawable.date_off),
    ON("All day", R.drawable.date_on);

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
    OFF("Don't repeat", R.drawable.repeat_off),
    WEEKLY("Repeat weekly", R.drawable.repeat_weekly),
    MONTHLY("Repeat monthly", R.drawable.repeat_monthly);

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
    OFF("Don't remind me", R.drawable.remind_off),
    ON("Remind me", R.drawable.remind_on);

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
// Sound
enum class UserSoundSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF("Alarm off", R.drawable.sound_off),
    ONCE("Alarm once", R.drawable.sound_once),
    CONTINUOUS("Alarm continuous", R.drawable.sound_continuous);

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
    OFF("Note off", R.drawable.note_off),
    ON("Note on", R.drawable.note_on);

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
    OFF("Vibrate off", R.drawable.vibrate_off),
    ONCE("Vibrate once", R.drawable.vibrate_once),
    CONTINUOUS("Vibrate continuous", R.drawable.vibrate_continuous);

    fun toggle(): UserVibrateSelection = when (this) {
        OFF -> ONCE
        ONCE -> CONTINUOUS
        CONTINUOUS -> OFF
    }
}

// Location
// Location
enum class UserLocationSelection(
    override val headerText: String,
    override val iconRes: Int
) : SectionMeta {
    OFF("No location", R.drawable.location_off),
    ON("With Location", R.drawable.location_on);

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
    OFF("No image", R.drawable.image_off),
    ON("With image", R.drawable.image_on);

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
    OFF("Default color", R.drawable.color_off),
    ON("Custom color", R.drawable.color_on);

    fun toggle(): UserColorSelection = when (this) {
        OFF -> ON
        ON -> OFF
    }
}
enum class UserColorChoices { DEFAULT, RED, GREEN, YELLOW, CYAN, MAGENTA }

