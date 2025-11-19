package com.example.bghelp.ui.screens.task.add

import android.content.Context
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.ui.theme.TaskDefault
import com.example.bghelp.utils.TaskImageStorage
import com.example.bghelp.utils.TaskMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

class SaveTaskHandler(
    private val taskRepository: TaskRepository,
    private val colorRepository: ColorRepository,
    private val appContext: Context
) {
    suspend fun saveTask(
        formState: AddTaskFormState,
        validationState: AddTaskValidationState
    ): SaveTaskResult {
        val validationError = validationState.validateForm(formState)
        if (validationError != null) {
            return SaveTaskResult.ValidationError(validationError)
        }
        
        return try {
            val imageAttachment = withContext(Dispatchers.IO) {
                TaskImageStorage.persistIfNeeded(
                    context = appContext,
                    isOn = formState.imageSelection == UserImageSelection.ON,
                    selected = formState.selectedImage
                )
            }
            
            val allDay = formState.dateSelection == UserDateSelection.ON
            val startDate = formState.startDate
            val startTime = if (allDay) LocalTime.MIDNIGHT else formState.startTime
            val endDateVisible = formState.isEndDateVisible
            val endTimeVisible = formState.isEndTimeVisible
            val endDate = if (endDateVisible) formState.endDate else null
            val endTime = if (endTimeVisible) formState.endTime else null
            
            val selectedColor = getSelectedTaskColor(formState)
            
            val assembleResult = AddTaskAssembler.buildOrError(
                title = formState.title,
                dateSelection = formState.dateSelection,
                startDate = startDate,
                startTime = startTime,
//                repeatSelection = formState.repeatSelection,
//                weeklyDays = formState.weeklySelectedDays,
//                monthlyDaySelection = formState.monthlyDaySelection,
//                monthlyDays = formState.monthlySelectedDays,
                remindSelection = formState.remindSelection,
                startReminders = formState.startReminders,
                endReminders = formState.endReminders,
                endDate = endDate,
                isEndDateVisible = endDateVisible,
                endTime = endTime,
                isEndTimeVisible = endTimeVisible,
                rrule = formState.repeatRRule,
                alarmName = formState.selectedAudioFile.takeIf { it.isNotBlank() },
                soundMode = with(TaskMapper) { formState.soundSelection.toAlarmMode() },
                vibrateMode = with(TaskMapper) { formState.vibrateSelection.toAlarmMode() },
                soundUri = formState.selectedAudioFile.takeIf { it.isNotBlank() },
                color = selectedColor,
                image = imageAttachment,
                description = formState.info.takeIf { it.isNotBlank() },
                note = if (formState.noteSelection == UserNoteSelection.ON)
                    formState.note.takeIf { it.isNotBlank() } else null,
                locations = formState.selectedLocations
            )
            
            when (assembleResult) {
                is AddTaskAssembler.BuildResult.Error -> {
                    SaveTaskResult.ValidationError(assembleResult.message)
                }
                is AddTaskAssembler.BuildResult.Success -> {
                    taskRepository.addTask(assembleResult.task)
                    SaveTaskResult.Success
                }
            }
        } catch (t: Throwable) {
            SaveTaskResult.Error(t)
        }
    }
    
    private suspend fun getSelectedTaskColor(formState: AddTaskFormState): FeatureColor {
        val colorId = if (formState.colorSelection == UserColorSelection.OFF) {
            1
        } else {
            formState.selectedColorId ?: 1
        }
        
        return colorRepository.getColorById(colorId)
            ?: FeatureColor(
                id = 1,
                name = AddTaskStrings.DEFAULT,
                red = (TaskDefault.red * 255).toInt(),
                green = (TaskDefault.green * 255).toInt(),
                blue = (TaskDefault.blue * 255).toInt(),
                alpha = TaskDefault.alpha
            )
    }
}

sealed interface SaveTaskResult {
    data object Success : SaveTaskResult
    data class ValidationError(val message: String) : SaveTaskResult
    data class Error(val throwable: Throwable) : SaveTaskResult
}

