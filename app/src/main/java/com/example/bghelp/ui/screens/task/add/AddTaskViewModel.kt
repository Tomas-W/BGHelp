package com.example.bghelp.ui.screens.task.add

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.R
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.TimeUnit
import com.example.bghelp.utils.AudioManager
import com.example.bghelp.utils.RepeatRuleBuilder
import com.example.bghelp.utils.TaskImageStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    val audioManager: AudioManager,
    private val taskRepository: TaskRepository,
    private val colorRepository: ColorRepository,
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {
    // STATES & HANDLERS
    private val formStateHolder = AddTaskFormStateHolder(
        context = appContext,
        onRepeatRuleChanged = { refreshRepeatRule() }
    )

    val formState: StateFlow<AddTaskFormState> = formStateHolder.formState

    private val validationState = AddTaskValidationState(
        formState = formState,
        scope = viewModelScope,
        context = appContext
    )

    private val saveTaskHandler = SaveTaskHandler(
        taskRepository = taskRepository,
        colorRepository = colorRepository,
        appContext = appContext
    )
    // STATES & HANDLERS

    // TITLE
    val titleText: StateFlow<String> = formState.map { it.title }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val infoText: StateFlow<String> = formState.map { it.info }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _userTitleSelection = MutableStateFlow(UserTitleSelection.OFF)
    val userTitleSelection: StateFlow<UserTitleSelection> = _userTitleSelection.asStateFlow()

    private val _activeTitleInput = MutableStateFlow<TitleInputType?>(null)
    val activeTitleInput: StateFlow<TitleInputType?> = _activeTitleInput.asStateFlow()

    fun toggleTitleSelection() {
        _userTitleSelection.value = _userTitleSelection.value.toggle()
    }

    fun setTitleText(text: String) {
        formStateHolder.updateTitle(text)
    }

    fun setInfoText(text: String) {
        formStateHolder.updateInfo(text)
    }

    fun setActiveTitleInput(type: TitleInputType) {
        _activeTitleInput.value = type
        setCalendarVisible(false)
        clearTimeInputSelection()
        clearReminderInputSelection()
        _keyboardDismissKey.value++
    }

    fun clearTitleInputSelection() {
        _activeTitleInput.value = null
    }
    // TITLE

    // DATE
    val userDateSelection: StateFlow<UserDateSelection> = formState.map { it.dateSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserDateSelection.OFF)

    val dateStartSelection: StateFlow<LocalDate> = formState.map { it.startDate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now().plusDays(1))

    val dateEndSelection: StateFlow<LocalDate?> = formState.map { it.endDate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isEndDateVisible: StateFlow<Boolean> = combine(
        formState.map { it.isEndDateVisible },
        formState.map { it.repeatSelection }
    ) { isEndDateVisible, repeatSelection ->
        // End date cannot be visible when repeat is active
        if (repeatSelection == UserRepeatSelection.WEEKLY || repeatSelection == UserRepeatSelection.MONTHLY) {
            false
        } else {
            isEndDateVisible
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val timeStartSelection: StateFlow<LocalTime> = formState.map { it.startTime }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalTime.of(12, 0))

    val timeEndSelection: StateFlow<LocalTime?> = formState.map { it.endTime }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isEndTimeVisible: StateFlow<Boolean> = formState.map { it.isEndTimeVisible }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private var hiddenDateEndValue: LocalDate? = null // Saves when endDate is off
    private var hiddenTimeEndValue: LocalTime? = null // Saves when endTime is off

    private val _isCalendarVisible = MutableStateFlow(false)
    val isCalendarVisible: StateFlow<Boolean> = _isCalendarVisible.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _activeDateField = MutableStateFlow<DateField?>(null)
    val activeDateField: StateFlow<DateField?> = _activeDateField.asStateFlow()

    private val _activeTimeSegment = MutableStateFlow<TimeSegment?>(null)
    val activeTimeSegment: StateFlow<TimeSegment?> = _activeTimeSegment.asStateFlow()

    private val _activeTimeField = MutableStateFlow<TimeField?>(null)
    val activeTimeField: StateFlow<TimeField?> = _activeTimeField.asStateFlow()

    fun toggleDateSelection() {
        formStateHolder.toggleDateSelection()
    }

    fun setDateStart(date: LocalDate) {
        formStateHolder.setDateStart(date)
    }

    fun toggleEndDateVisible() {
        val state = formState.value
        // Prevent toggling end date when repeat is active
        if (state.repeatSelection == UserRepeatSelection.WEEKLY || state.repeatSelection == UserRepeatSelection.MONTHLY) {
            showSnackbar(appContext.getString(R.string.task_date_range_not_allowed_with_repeat))
            return
        }
        if (state.isEndDateVisible) {
            hiddenDateEndValue = state.endDate
            formStateHolder.toggleEndDateVisible()
            if (_activeDateField.value == DateField.END) {
                setCalendarVisible(false)
            }
        } else {
            formStateHolder.toggleEndDateVisible(restoreDate = hiddenDateEndValue)
        }
    }

    fun setDateEnd(date: LocalDate) {
        formStateHolder.setDateEnd(date)
        hiddenDateEndValue = date
    }

    fun setTimeStart(time: LocalTime) {
        formStateHolder.setTimeStart(time)
    }

    fun toggleEndTimeVisible() {
        val state = formState.value
        if (state.isEndTimeVisible) {
            hiddenTimeEndValue = state.endTime
            formStateHolder.toggleEndTimeVisible()
            if (_activeTimeField.value == TimeField.END) {
                clearTimeInputSelection()
            }
        } else {
            formStateHolder.toggleEndTimeVisible(restoreTime = hiddenTimeEndValue)
        }
    }

    fun setTimeEnd(time: LocalTime) {
        formStateHolder.setTimeEnd(time)
        hiddenTimeEndValue = time
    }

    fun setActiveTimeInput(field: TimeField?, segment: TimeSegment?) {
        _activeTimeField.value = field
        _activeTimeSegment.value = segment
        if (field != null) {
            clearReminderInputSelection()
            clearTitleInputSelection()
        }
    }

    fun onTimeInputClicked() {
        setCalendarVisible(false)
        clearReminderInputSelection()
        clearTitleInputSelection()
    }

    fun clearTimeInputSelection() {
        _activeTimeField.value = null
        _activeTimeSegment.value = null
    }

    fun setCalendarVisible(visible: Boolean) {
        _isCalendarVisible.value = visible
        if (!visible) {
            _activeDateField.value = null
        }
    }

    fun toggleCalendarFromDateField(field: DateField) {
        if (_isCalendarVisible.value && _activeDateField.value == field) {
            setCalendarVisible(false)
        } else {
            _activeDateField.value = field
            setCalendarVisible(true)
            clearReminderInputSelection()
            clearTimeInputSelection()
            _keyboardDismissKey.value++
        }
    }

    fun setCurrentMonth(month: YearMonth) {
        _currentMonth.value = month
    }

    fun onDayClicked(clickedDate: LocalDate) {
        val state = formState.value
        if (!state.isEndDateVisible) {
            setDateStart(clickedDate)
        } else {
            when (_activeDateField.value) {
                DateField.START -> setDateStart(clickedDate)
                DateField.END -> setDateEnd(clickedDate)
                null -> {}
            }
        }
        YearMonth.from(clickedDate).takeIf { it != _currentMonth.value }?.let {
            setCurrentMonth(it)
        }
    }
    // DATE

    // REPEAT
    val userRepeatSelection: StateFlow<UserRepeatSelection> = formState.map { it.repeatSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRepeatSelection.OFF)

    val weeklySelectedDays: StateFlow<Set<Int>> = formState.map { it.weeklySelectedDays }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf(1, 2, 3, 4, 5, 6, 7))

    val weeklyIntervalWeeks: StateFlow<Int> = formState.map { it.weeklyIntervalWeeks }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val monthlySelectedMonths: StateFlow<Set<Int>> = formState.map { it.monthlySelectedMonths }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), (1..12).toSet())

    val monthlySelectedDays: StateFlow<Set<Int>> = formState.map { it.monthlySelectedDays }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), (1..31).toSet())

    val userMonthlyDaySelection: StateFlow<RepeatMonthlyDaySelection> =
        formState.map { it.monthlyDaySelection }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                RepeatMonthlyDaySelection.ALL
            )

    fun toggleRepeatSelection() {
        val state = formState.value
        val newRepeatSelection = state.repeatSelection.toggle()
        
        // If turning on repeat (WEEKLY or MONTHLY), hide end date
        if ((newRepeatSelection == UserRepeatSelection.WEEKLY || newRepeatSelection == UserRepeatSelection.MONTHLY) 
            && state.isEndDateVisible) {
            hiddenDateEndValue = state.endDate
            formStateHolder.toggleEndDateVisible()
            if (_activeDateField.value == DateField.END) {
                setCalendarVisible(false)
            }
        }
        
        formStateHolder.toggleRepeatSelection()
    }

    fun toggleWeeklySelectedDays(day: Int) {
        formStateHolder.toggleWeeklySelectedDays(day)
    }

    fun setWeeklyIntervalWeeks(weeks: Int) {
        formStateHolder.setWeeklyIntervalWeeks(weeks)
    }

    fun toggleMonthlySelectedMonths(month: Int) {
        formStateHolder.toggleMonthlySelectedMonths(month)
    }

    fun toggleMonthSelectedDays(day: Int) {
        formStateHolder.toggleMonthSelectedDays(day)
    }

    fun selectAllMonthlySelectedMonths() {
        formStateHolder.selectAllMonthlySelectedMonths()
    }

    fun deselectAllMonthlySelectedMonths() {
        formStateHolder.deselectAllMonthlySelectedMonths()
    }

    fun toggleMonthlyDaySelection(newSelection: RepeatMonthlyDaySelection) {
        formStateHolder.toggleMonthlyDaySelection(newSelection)
    }

    fun selectAllMonthlySelectedDays() {
        formStateHolder.selectAllMonthlySelectedDays()
    }

    fun deselectAllMonthlySelectedDays() {
        formStateHolder.deselectAllMonthlySelectedDays()
    }
    // REPEAT

    // RRULE
    private fun refreshRepeatRule() {
        val state = formState.value
        formStateHolder.updateRepeatRRule(
            RepeatRuleBuilder.buildRRule(
                selection = state.repeatSelection,
                weeklyDays = state.weeklySelectedDays,
                weeklyInterval = state.weeklyIntervalWeeks,
                monthlyMonths = state.monthlySelectedMonths,
                monthlyDaySelection = state.monthlyDaySelection,
                monthlyDays = state.monthlySelectedDays,
                endDate = if (state.isEndDateVisible) state.endDate else null,
                isEndDateVisible = state.isEndDateVisible
            )
        )
    }
    // RRULE

    // REMIND
    val userRemindSelection: StateFlow<UserRemindSelection> = formState.map { it.remindSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRemindSelection.OFF)

    val startReminders: StateFlow<List<Reminder>> = formState.map { it.startReminders }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val endReminders: StateFlow<List<Reminder>> = formState.map { it.endReminders }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var nextReminderId = 0

    private val _activeReminderInput = MutableStateFlow<ActiveReminderInput?>(null)
    val activeReminderInput: StateFlow<ActiveReminderInput?> = _activeReminderInput.asStateFlow()

    fun toggleRemindSelection() {
        formStateHolder.toggleRemindSelection()
    }

    fun addReminder(type: RemindType) {
        nextReminderId = formStateHolder.addReminder(type, nextReminderId)
    }

    fun removeReminder(type: RemindType, reminderId: Int) {
        if (_activeReminderInput.value?.type == type && _activeReminderInput.value?.id == reminderId) {
            clearReminderInputSelection()
        }
        formStateHolder.removeReminder(type, reminderId)
    }

    fun updateReminder(
        reminderType: RemindType,
        reminderId: Int,
        value: Int? = null,
        timeUnit: TimeUnit? = null
    ) {
        formStateHolder.updateReminder(reminderType, reminderId, value, timeUnit)
    }

    fun setActiveSnoozeInput(snoozeIndex: Int) {
        _activeReminderInput.value = ActiveReminderInput(RemindType.START, -1, snoozeIndex)
        setCalendarVisible(false)
        clearTimeInputSelection()
        clearTitleInputSelection()
        _keyboardDismissKey.value++
    }

    fun setActiveReminderInput(type: RemindType, id: Int) {
        _activeReminderInput.value = ActiveReminderInput(type, id)
        setCalendarVisible(false)
        clearTimeInputSelection()
        clearTitleInputSelection()
        _keyboardDismissKey.value++
    }

    fun clearReminderInputSelection() {
        _activeReminderInput.value = null
    }

    fun updateSnooze(snoozeIndex: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        formStateHolder.updateSnooze(snoozeIndex, value, timeUnit)
    }
    // REMIND

    // SOUND
    val userSoundSelection: StateFlow<UserSoundSelection> = formState.map { it.soundSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSoundSelection.OFF)

    val selectedAudioFile: StateFlow<String> = formState.map { it.selectedAudioFile }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun toggleSoundSelection() {
        formStateHolder.toggleSoundSelection()
    }

    fun setSelectedAudioFile(fileName: String) {
        formStateHolder.setSelectedAudioFile(fileName)
    }
    // SOUND

    // VIBRATE
    val userVibrateSelection: StateFlow<UserVibrateSelection> =
        formState.map { it.vibrateSelection }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserVibrateSelection.OFF)

    fun toggleVibrateSelection() {
        formStateHolder.toggleVibrateSelection()
    }
    // VIBRATE

    // SNOOZE
    val snoozeValue1: StateFlow<Int> = formState.map { it.snoozeValue1 }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddTaskConstants.SNOOZE_A_VALUE
        )

    val snoozeUnit1: StateFlow<TimeUnit> = formState.map { it.snoozeUnit1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeUnit.MINUTES)

    val snoozeValue2: StateFlow<Int> = formState.map { it.snoozeValue2 }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddTaskConstants.SNOOZE_B_VALUE
        )

    val snoozeUnit2: StateFlow<TimeUnit> = formState.map { it.snoozeUnit2 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeUnit.HOURS)
    // SNOOZE

    // NOTE
    val userNoteSelection: StateFlow<UserNoteSelection> = formState.map { it.noteSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserNoteSelection.OFF)

    val selectedNote: StateFlow<String> = formState.map { it.note }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun toggleNoteSelection() {
        formStateHolder.toggleNoteSelection()
    }

    fun setSelectedNote(note: String) {
        formStateHolder.updateNote(note)
    }
    // NOTE

    // LOCATION
    val userLocationSelection: StateFlow<UserLocationSelection> =
        formState.map { it.locationSelection }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                UserLocationSelection.OFF
            )

    val selectedLocations: StateFlow<List<TaskLocation>> = formState.map { it.selectedLocations }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLocationSelection() {
        formStateHolder.toggleLocationSelection()
    }

    fun setSelectedLocations(locations: List<TaskLocation>) {
        formStateHolder.setSelectedLocations(locations)
    }

    fun removeSelectedLocation(location: TaskLocation) {
        formStateHolder.removeSelectedLocation(location)
    }
    // LOCATION

    // IMAGE
    val userImageSelection: StateFlow<UserImageSelection> = formState.map { it.imageSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserImageSelection.OFF)

    val selectedImage: StateFlow<TaskImageData?> = formState.map { it.selectedImage }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleImageSelection() {
        formStateHolder.toggleImageSelection()
    }

    fun setImageFromGallery(uri: Uri, displayName: String?) {
        formStateHolder.setImageFromGallery(uri, displayName)
    }

    fun setImageFromCamera(bitmap: Bitmap, displayName: String? = null) {
        // Save temporary image
        viewModelScope.launch {
            try {
                val imageDisplayName = displayName ?: appContext.getString(R.string.task_captured_image)
                val tempUri = TaskImageStorage.saveTemporaryImage(
                    context = appContext,
                    bitmap = bitmap,
                    displayName = imageDisplayName
                )
                // Recycle after saving
                bitmap.recycle()
                // Store URI
                formStateHolder.setImageFromCamera(tempUri, imageDisplayName)
            } catch (e: Exception) {
                bitmap.recycle()
                // Clear image selection and show error
                formStateHolder.clearImage()
                showSnackbar(appContext.getString(R.string.snackbar_add_image_failed))
            }
        }
    }
    // IMAGE

    // COLOR
    val userColorSelection: StateFlow<UserColorSelection> = formState.map { it.colorSelection }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserColorSelection.OFF)

    val selectedColorId: StateFlow<Int?> = formState.map { it.selectedColorId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allColors: StateFlow<List<FeatureColor>> = colorRepository.getAllColors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun toggleColorSelection() {
        formStateHolder.toggleColorSelection()
    }

    fun setSelectedColorId(colorId: Int?) {
        formStateHolder.setSelectedColorId(colorId)
    }
    // COLOR

    // SAVE TASK
    fun loadTaskForEditingById(taskId: Int) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)
                .first()
                ?.let { task ->
                    _editingTaskId.value = task.id
                    formStateHolder.loadFromTask(task)
                    _currentMonth.value = YearMonth.from(task.date)
                }
        }
    }

    fun loadTaskAsSingleOccurrence(taskId: Int, occurrenceDate: LocalDateTime) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)
                .first()
                ?.let { baseTask ->
                    // Create a task with the occurrence date but base task data
                    val taskWithOccurrenceDate = baseTask.copy(
                        date = occurrenceDate,
                        endDate = baseTask.endDate?.let { endDate ->
                            val duration = java.time.Duration.between(baseTask.date, endDate)
                            occurrenceDate.plus(duration)
                        }
                    )
                    // Don't set editingTaskId - we'll save as new task
                    _editingTaskId.value = null
                    formStateHolder.loadFromTaskAsSingleOccurrence(taskWithOccurrenceDate)
                    _currentMonth.value = YearMonth.from(occurrenceDate)
                }
        }
    }

    fun saveTask() {
        if (_saveState.value == SaveTaskState.Saving) return

        _saveState.value = SaveTaskState.Saving
        viewModelScope.launch {
            val taskId = _editingTaskId.value
            val occurrenceInfo = _editingOccurrenceInfo.value
            val result = when {
                occurrenceInfo != null -> {
                    // Save as single occurrence (new task + exclude from base)
                    saveTaskHandler.saveTaskAsSingleOccurrence(
                        baseTaskId = occurrenceInfo.first,
                        occurrenceDate = occurrenceInfo.second,
                        formState = formState.value,
                        validationState = validationState
                    )
                }
                taskId != null -> {
                    // Regular edit - update existing task
                    saveTaskHandler.updateTask(taskId, formState.value, validationState)
                }
                else -> {
                    // New task
                    saveTaskHandler.saveTask(formState.value, validationState)
                }
            }

            when (result) {
                is SaveTaskResult.Success -> {
                    _savedTaskInfo.value = result.taskId to result.taskDate
                    _saveState.value = SaveTaskState.Success
                    if (taskId != null) {
                        _editingTaskId.value = null
                    }
                    if (occurrenceInfo != null) {
                        _editingOccurrenceInfo.value = null
                    }
                }

                is SaveTaskResult.ValidationError -> {
                    showSnackbar(result.message)
                    _saveState.value = SaveTaskState.Idle
                }

                is SaveTaskResult.Error -> {
                    _saveState.value = SaveTaskState.Error(result.throwable)
                }
            }
        }
    }

    val isNewTaskValid: StateFlow<Boolean> = validationState.isFormValid

    fun getValidationError(): String? {
        return validationState.validateForm(formState.value)
    }

    fun consumeSaveState() {
        _saveState.value = SaveTaskState.Idle
        _savedTaskInfo.value = null
    }

    private val _saveState = MutableStateFlow<SaveTaskState>(SaveTaskState.Idle)
    val saveState: StateFlow<SaveTaskState> = _saveState.asStateFlow()

    private val _savedTaskInfo = MutableStateFlow<Pair<Int, LocalDateTime>?>(null)
    val savedTaskInfo: StateFlow<Pair<Int, LocalDateTime>?> = _savedTaskInfo.asStateFlow()

    private val _editingTaskId = MutableStateFlow<Int?>(null)
    val isEditing: StateFlow<Boolean> = _editingTaskId.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _editingOccurrenceInfo = MutableStateFlow<Pair<Int, LocalDateTime>?>(null)
    val editingOccurrenceInfo: StateFlow<Pair<Int, LocalDateTime>?> = _editingOccurrenceInfo.asStateFlow()

    fun setEditingOccurrenceInfo(baseTaskId: Int, occurrenceDate: LocalDateTime) {
        _editingOccurrenceInfo.value = baseTaskId to occurrenceDate
    }

    fun clearEditingState() {
        _editingTaskId.value = null
        _editingOccurrenceInfo.value = null
    }
    // SAVE TASK

    // RESET DISMISS CLEAR
    private val _showUndoResetForm = MutableStateFlow(false)
    val showUndoResetForm: StateFlow<Boolean> = _showUndoResetForm.asStateFlow()

    private data class FormStateSnapshot(
        val formState: AddTaskFormState,
        val nextReminderId: Int,
        val hiddenDateEndValue: LocalDate?,
        val hiddenTimeEndValue: LocalTime?
    )

    private var savedStateSnapshot: FormStateSnapshot? = null

    fun resetForm() {
        // Save current state before resetting (including image URI)
        savedStateSnapshot = FormStateSnapshot(
            formState = formState.value,
            nextReminderId = nextReminderId,
            hiddenDateEndValue = hiddenDateEndValue,
            hiddenTimeEndValue = hiddenTimeEndValue
        )

        formStateHolder.reset()
        resetUIState()
        refreshRepeatRule()
        _showUndoResetForm.value = true
    }

    fun undoResetForm() {
        savedStateSnapshot?.let { snapshot ->
            formStateHolder.restore(snapshot.formState)
            nextReminderId = snapshot.nextReminderId
            hiddenDateEndValue = snapshot.hiddenDateEndValue
            hiddenTimeEndValue = snapshot.hiddenTimeEndValue
            refreshRepeatRule()
            _showUndoResetForm.value = false
            savedStateSnapshot = null
        }
    }

    fun hideUndoResetForm() {
        _showUndoResetForm.value = false
    }

    fun cleanUpResetState() {
        savedStateSnapshot = null
        TaskImageStorage.cleanupTemporaryImages(appContext)
    }

    private fun resetUIState() {
        _userTitleSelection.value = UserTitleSelection.OFF
        _activeTitleInput.value = null
        _isCalendarVisible.value = false
        _currentMonth.value = YearMonth.now()
        _activeDateField.value = null
        _activeTimeSegment.value = null
        _activeTimeField.value = null
        _activeReminderInput.value = null
        nextReminderId = 0
        hiddenDateEndValue = null
        hiddenTimeEndValue = null
        _saveState.value = SaveTaskState.Idle
        _snackbarMessage.value = null
    }

    private val _keyboardDismissKey = MutableStateFlow(0)
    val keyboardDismissKey: StateFlow<Int> = _keyboardDismissKey.asStateFlow()

    fun clearNonCalendarInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        clearTitleInputSelection()
    }

    fun clearAllInputSelections() {
        clearNonCalendarInputSelections()
        setCalendarVisible(false)
    }
    // RESET DISMISS CLEAR

    // SNACKBAR
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    // SNACKBAR

    init {
        refreshRepeatRule()
    }
}
