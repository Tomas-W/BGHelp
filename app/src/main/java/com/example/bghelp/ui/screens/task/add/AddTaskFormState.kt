package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.net.Uri
import com.example.bghelp.domain.model.FeatureColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class AddTaskFormState(
    val title: String = "",
    val info: String = "",
    
    val dateSelection: UserDateSelection = UserDateSelection.OFF,
    val startDate: LocalDate = LocalDate.now().plusDays(1),
    val endDate: LocalDate? = null,
    val isEndDateVisible: Boolean = false,
    val startTime: LocalTime = LocalTime.of(12, 0),
    val endTime: LocalTime? = null,
    val isEndTimeVisible: Boolean = false,
    
    val repeatSelection: UserRepeatSelection = UserRepeatSelection.OFF,
    val weeklySelectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val weeklyIntervalWeeks: Int = 1,
    val monthlySelectedMonths: Set<Int> = (1..12).toSet(),
    val monthlyDaySelection: RepeatMonthlyDaySelection = RepeatMonthlyDaySelection.ALL,
    val monthlySelectedDays: Set<Int> = (1..31).toSet(),
    val repeatRRule: String? = null,
    
    val remindSelection: UserRemindSelection = UserRemindSelection.OFF,
    val startReminders: List<Reminder> = emptyList(),
    val endReminders: List<Reminder> = emptyList(),
    
    val soundSelection: UserSoundSelection = UserSoundSelection.OFF,
    val selectedAudioFile: String = "",
    val vibrateSelection: UserVibrateSelection = UserVibrateSelection.OFF,
    
    val noteSelection: UserNoteSelection = UserNoteSelection.OFF,
    val note: String = "",
    
    val locationSelection: UserLocationSelection = UserLocationSelection.OFF,
    val selectedLocations: List<TaskLocation> = emptyList(),
    
    val imageSelection: UserImageSelection = UserImageSelection.OFF,
    val selectedImage: TaskImageData? = null,
    
    val colorSelection: UserColorSelection = UserColorSelection.OFF,
    val selectedColorId: Int? = null
) {
    companion object {
        fun default() = AddTaskFormState()
    }
}

