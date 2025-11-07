package com.example.bghelp.ui.screens.task.add

import androidx.lifecycle.ViewModel
import com.example.bghelp.utils.AudioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    val audioManager: AudioManager
) : ViewModel() {
    
    // Title selection
    private val _userTitleSelection = MutableStateFlow(UserTitleSelection.OFF)
    val userTitleSelection: StateFlow<UserTitleSelection> = _userTitleSelection.asStateFlow()
    // Title
    private val _titleText = MutableStateFlow("")
    val titleText: StateFlow<String> = _titleText.asStateFlow()
    // Info
    private val _infoText = MutableStateFlow("")
    val infoText: StateFlow<String> = _infoText.asStateFlow()
    // Title Info tracking
    private val _activeTitleInput = MutableStateFlow<TitleInputType?>(null)
    val activeTitleInput: StateFlow<TitleInputType?> = _activeTitleInput.asStateFlow()

    // When selection
    private val _userDateSelection = MutableStateFlow(UserDateSelection.OFF)
    val userDateSelection: StateFlow<UserDateSelection> = _userDateSelection.asStateFlow()
    // Date
    private val _dateStartSelection = MutableStateFlow(LocalDate.now())
    val dateStartSelection: StateFlow<LocalDate> = _dateStartSelection.asStateFlow()
    private val _dateEndSelection = MutableStateFlow<LocalDate?>(null)
    val dateEndSelection: StateFlow<LocalDate?> = _dateEndSelection.asStateFlow()
    private val _isEndDateVisible = MutableStateFlow(false)
    val isEndDateVisible: StateFlow<Boolean> = _isEndDateVisible.asStateFlow()
    // Time
    private val _timeStartSelection = MutableStateFlow(LocalTime.now().withSecond(0).withNano(0))
    val timeStartSelection: StateFlow<LocalTime> = _timeStartSelection.asStateFlow()
    private val _timeEndSelection = MutableStateFlow<LocalTime?>(null)
    val timeEndSelection: StateFlow<LocalTime?> = _timeEndSelection.asStateFlow()
    private val _isEndTimeVisible = MutableStateFlow(false)
    val isEndTimeVisible: StateFlow<Boolean> = _isEndTimeVisible.asStateFlow()
    // To save/load when toggling endDate or endTime
    private var hiddenDateEndValue: LocalDate? = null
    private var hiddenTimeEndValue: LocalTime? = null
    // Calendar state
    private val _isCalendarVisible = MutableStateFlow(false)
    val isCalendarVisible: StateFlow<Boolean> = _isCalendarVisible.asStateFlow()
    // Date
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()
    // Date field
    private val _activeDateField = MutableStateFlow<DateField?>(null)
    val activeDateField: StateFlow<DateField?> = _activeDateField.asStateFlow()
    // Time
    private val _activeTimeSegment = MutableStateFlow<TimeSegment?>(null)
    val activeTimeSegment: StateFlow<TimeSegment?> = _activeTimeSegment.asStateFlow()
    // Time field
    private val _activeTimeField = MutableStateFlow<TimeField?>(null)
    val activeTimeField: StateFlow<TimeField?> = _activeTimeField.asStateFlow()
    // Time validation
    private val _isTimeRangeInvalid = MutableStateFlow(false)
    val isTimeRangeInvalid: StateFlow<Boolean> = _isTimeRangeInvalid.asStateFlow()

    // Repeat selection
    private val _userRepeatSelection = MutableStateFlow(UserRepeatSelection.OFF)
    val userRepeatSelection: StateFlow<UserRepeatSelection> = _userRepeatSelection.asStateFlow()
    // Weekly
    private val _weeklySelectedDays = MutableStateFlow(setOf(1, 2, 3, 4, 5, 6, 7))
    val weeklySelectedDays: StateFlow<Set<Int>> = _weeklySelectedDays.asStateFlow()
    private val _weeklyIntervalWeeks = MutableStateFlow(1)
    val weeklyIntervalWeeks: StateFlow<Int> = _weeklyIntervalWeeks.asStateFlow()
    private val _weeklyUntilSelection = MutableStateFlow(RepeatUntilSelection.FOREVER)
    val weeklyUntilSelection: StateFlow<RepeatUntilSelection> = _weeklyUntilSelection.asStateFlow()
    private val _weeklyUntilDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val weeklyUntilDate: StateFlow<LocalDate> = _weeklyUntilDate.asStateFlow()
    // Monthly
    private val _monthlySelectedMonths = MutableStateFlow((1..12).toSet())
    val monthlySelectedMonths: StateFlow<Set<Int>> = _monthlySelectedMonths.asStateFlow()
    private val _monthlySelectedDays = MutableStateFlow((1..31).toSet())
    val monthlySelectedDays: StateFlow<Set<Int>> = _monthlySelectedDays.asStateFlow()
    private val _monthlyUntilSelection = MutableStateFlow(RepeatUntilSelection.FOREVER)
    val monthlyUntilSelection: StateFlow<RepeatUntilSelection> = _monthlyUntilSelection.asStateFlow()
    private val _monthlyUntilDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val monthlyUntilDate: StateFlow<LocalDate> = _monthlyUntilDate.asStateFlow()
    // Calendar
    private val _isUntilCalendarVisible = MutableStateFlow(false)
    val isUntilCalendarVisible: StateFlow<Boolean> = _isUntilCalendarVisible.asStateFlow()
    private val _untilCalendarMonth = MutableStateFlow(YearMonth.now())
    val untilCalendarMonth: StateFlow<YearMonth> = _untilCalendarMonth.asStateFlow()
    private val _activeUntilCalendarContext = MutableStateFlow<RepeatUntilContext?>(null)
    val activeUntilCalendarContext: StateFlow<RepeatUntilContext?> = _activeUntilCalendarContext.asStateFlow()

    // Reminder selection
    private val _userRemindSelection = MutableStateFlow(UserRemindSelection.OFF)
    val userRemindSelection: StateFlow<UserRemindSelection> = _userRemindSelection.asStateFlow()
    // Start reminders
    private val _startReminders = MutableStateFlow<List<Reminder>>(emptyList())
    val startReminders: StateFlow<List<Reminder>> = _startReminders.asStateFlow()
    // End reminders
    private val _endReminders = MutableStateFlow<List<Reminder>>(emptyList())
    val endReminders: StateFlow<List<Reminder>> = _endReminders.asStateFlow()
    // Reminder tracking
    private var nextReminderId = 0
    data class ActiveReminderInput(val type: RemindType, val id: Int)
    private val _activeReminderInput = MutableStateFlow<ActiveReminderInput?>(null)
    val activeReminderInput: StateFlow<ActiveReminderInput?> = _activeReminderInput.asStateFlow()
    // Sound selection
    private val _userSoundSelection = MutableStateFlow(UserSoundSelection.OFF)
    val userSoundSelection: StateFlow<UserSoundSelection> = _userSoundSelection.asStateFlow()
    // Selected audio file
    private val _selectedAudioFile = MutableStateFlow<String>("")
    val selectedAudioFile: StateFlow<String> = _selectedAudioFile.asStateFlow()
    // Vibrate selection
    private val _userVibrateSelection = MutableStateFlow(UserVibrateSelection.OFF)
    val userVibrateSelection: StateFlow<UserVibrateSelection> = _userVibrateSelection.asStateFlow()

    // Color
    private val _userColorSelection = MutableStateFlow(UserColorSelection.OFF)
    val userColorSelection: StateFlow<UserColorSelection> = _userColorSelection.asStateFlow()
    private val _selectedColor = MutableStateFlow(UserColorChoices.DEFAULT)
    val selectedColor: StateFlow<UserColorChoices> = _selectedColor.asStateFlow()

    // Keyboard dismiss tracking
    private val _keyboardDismissKey = MutableStateFlow(0)
    val keyboardDismissKey: StateFlow<Int> = _keyboardDismissKey.asStateFlow()

    init {
        updateTimeValidationState()
    }

    private fun updateTimeValidationState() {
        val endDate = _dateEndSelection.value
        val endTime = _timeEndSelection.value
        
        if (endTime == null) {
            _isTimeRangeInvalid.value = false
            return
        }
        if (endDate == null) {
            // Same day validation
            _isTimeRangeInvalid.value = endTime.isBefore(_timeStartSelection.value)
            return
        }
        // Different days validate
        val startDateTime = LocalDateTime.of(_dateStartSelection.value, _timeStartSelection.value)
        val endDateTime = LocalDateTime.of(endDate, endTime)
        _isTimeRangeInvalid.value = endDateTime.isBefore(startDateTime)
    }

    // Title
    fun toggleTitleSelection() {
        _userTitleSelection.value = _userTitleSelection.value.toggle()
    }
    fun setTitleText(text: String) {
        _titleText.value = text
    }
    fun setInfoText(text: String) {
        _infoText.value = text
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

    // When
    fun toggleDateSelection() {
        _userDateSelection.value = _userDateSelection.value.toggle()
    }
    fun setDateStart(date: LocalDate) {
        _dateStartSelection.value = date
        if (_isEndDateVisible.value && _dateEndSelection.value != null) {
            val endDate = _dateEndSelection.value!!
            if (date.isAfter(endDate)) {
                _dateEndSelection.value = date
                hiddenDateEndValue = date
            }
        }
        updateTimeValidationState()
    }
    fun toggleEndDateVisible() {
        if (_isEndDateVisible.value) {
            hiddenDateEndValue = _dateEndSelection.value
            _isEndDateVisible.value = false
            _dateEndSelection.value = null
            if (_activeDateField.value == DateField.END) {
                setCalendarVisible(false)
            }
        } else {
            _isEndDateVisible.value = true
            val initialEndDate = hiddenDateEndValue ?: _dateStartSelection.value
            _dateEndSelection.value = initialEndDate.coerceAtLeast(_dateStartSelection.value)
            hiddenDateEndValue = _dateEndSelection.value
        }
        updateTimeValidationState()
    }
    fun setDateEnd(date: LocalDate) {
        _dateEndSelection.value = date
        hiddenDateEndValue = date
        _dateStartSelection.value = _dateStartSelection.value.coerceAtMost(date)
        updateTimeValidationState()
    }
    fun setTimeStart(time: LocalTime) {
        _timeStartSelection.value = time
        updateTimeValidationState()
    }
    fun toggleEndTimeVisible() {
        if (_isEndTimeVisible.value) {
            hiddenTimeEndValue = _timeEndSelection.value
            _isEndTimeVisible.value = false
            _timeEndSelection.value = null
            // Clear selection if end time input is active
            if (_activeTimeField.value == TimeField.END) {
                clearTimeInputSelection()
            }
        } else {
            _isEndTimeVisible.value = true
            _timeEndSelection.value = hiddenTimeEndValue ?: _timeStartSelection.value.plusHours(1)
        }
        updateTimeValidationState()
    }
    fun setTimeEnd(time: LocalTime) {
        _timeEndSelection.value = time
        hiddenTimeEndValue = time
        updateTimeValidationState()
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

    // Calendar
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
        if (!_isEndDateVisible.value) {
            // Single day mode: always set start date
            setDateStart(clickedDate)
        } else {
            // Range mode: use active field
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

    // Repeat
    fun toggleRepeatSelection() {
        _userRepeatSelection.value = _userRepeatSelection.value.toggle()
    }
    fun toggleDaySelection(day: Int) {
        val currentDays = _weeklySelectedDays.value.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _weeklySelectedDays.value = currentDays
    }
    fun setWeeklyIntervalWeeks(weeks: Int) {
        _weeklyIntervalWeeks.value = weeks
    }
    fun toggleMonthSelection(month: Int) {
        val currentMonths = _monthlySelectedMonths.value.toMutableSet()
        if (currentMonths.contains(month)) {
            currentMonths.remove(month)
        } else {
            currentMonths.add(month)
        }
        _monthlySelectedMonths.value = currentMonths
    }
    fun toggleMonthDaySelection(day: Int) {
        val currentDays = _monthlySelectedDays.value.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _monthlySelectedDays.value = currentDays
    }
    fun setWeeklyUntilSelection(selection: RepeatUntilSelection) {
        _weeklyUntilSelection.value = selection
        if (selection == RepeatUntilSelection.FOREVER && _activeUntilCalendarContext.value == RepeatUntilContext.WEEKLY) {
            setUntilCalendarVisible(false)
        }
    }
    fun setMonthlyUntilSelection(selection: RepeatUntilSelection) {
        _monthlyUntilSelection.value = selection
        if (selection == RepeatUntilSelection.FOREVER && _activeUntilCalendarContext.value == RepeatUntilContext.MONTHLY) {
            setUntilCalendarVisible(false)
        }
    }
    fun toggleWeeklyUntilCalendar() {
        toggleUntilCalendar(RepeatUntilContext.WEEKLY)
    }
    fun toggleMonthlyUntilCalendar() {
        toggleUntilCalendar(RepeatUntilContext.MONTHLY)
    }
    private fun toggleUntilCalendar(context: RepeatUntilContext) {
        if (_isUntilCalendarVisible.value && _activeUntilCalendarContext.value == context) {
            setUntilCalendarVisible(false)
            return
        }

        val initialDate = when (context) {
            RepeatUntilContext.WEEKLY -> _weeklyUntilDate.value
            RepeatUntilContext.MONTHLY -> _monthlyUntilDate.value
        }
        _activeUntilCalendarContext.value = context
        YearMonth.from(initialDate).takeIf { it != _untilCalendarMonth.value }?.let {
            setUntilCalendarMonth(it)
        }
        setUntilCalendarVisible(true)
        setCalendarVisible(false)
        clearReminderInputSelection()
        clearTimeInputSelection()
        clearTitleInputSelection()
        _keyboardDismissKey.value++
    }
    private fun setUntilCalendarVisible(visible: Boolean) {
        _isUntilCalendarVisible.value = visible
        if (!visible) {
            _activeUntilCalendarContext.value = null
        }
    }
    fun setUntilCalendarMonth(month: YearMonth) {
        _untilCalendarMonth.value = month
    }
    fun onUntilDayClicked(clickedDate: LocalDate) {
        applyUntilCalendarDate(clickedDate)
    }
    private fun applyUntilCalendarDate(date: LocalDate) {
        when (_activeUntilCalendarContext.value) {
            RepeatUntilContext.WEEKLY -> {
                _weeklyUntilDate.value = date
                _weeklyUntilSelection.value = RepeatUntilSelection.DATE
            }
            RepeatUntilContext.MONTHLY -> {
                _monthlyUntilDate.value = date
                _monthlyUntilSelection.value = RepeatUntilSelection.DATE
            }
            null -> return
        }
        YearMonth.from(date).takeIf { it != _untilCalendarMonth.value }?.let {
            setUntilCalendarMonth(it)
        }
        setUntilCalendarVisible(false)
    }

    // Remind
    fun toggleRemindSelection() {
        _userRemindSelection.value = _userRemindSelection.value.toggle()
    }
    fun addReminder(type: RemindType) {
        val newReminder = Reminder(
            id = nextReminderId++,
            value = AddTaskConstants.REMINDER_START,
            timeUnit = TimeUnit.MINUTES
        )
        when (type) {
            RemindType.START -> {
                _startReminders.value = _startReminders.value + newReminder
            }
            RemindType.END -> {
                _endReminders.value = _endReminders.value + newReminder
            }
        }
    }
    fun removeReminder(type: RemindType, reminderId: Int) {
        // Clear selection if this reminder was active
        if (_activeReminderInput.value?.type == type && _activeReminderInput.value?.id == reminderId) {
            clearReminderInputSelection()
        }
        when (type) {
            RemindType.START -> {
                _startReminders.value = _startReminders.value.filter { it.id != reminderId }
            }
            RemindType.END -> {
                _endReminders.value = _endReminders.value.filter { it.id != reminderId }
            }
        }
    }
    fun updateReminder(type: RemindType, reminderId: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        val validatedValue = value?.coerceIn(AddTaskConstants.MIN_REMINDER, AddTaskConstants.MAX_REMINDER)

        when (type) {
            RemindType.START -> {
                _startReminders.value = _startReminders.value.map { reminder ->
                    if (reminder.id == reminderId) {
                        reminder.copy(
                            value = validatedValue ?: reminder.value,
                            timeUnit = timeUnit ?: reminder.timeUnit
                        )
                    } else {
                        reminder
                    }
                }
            }
            RemindType.END -> {
                _endReminders.value = _endReminders.value.map { reminder ->
                    if (reminder.id == reminderId) {
                        reminder.copy(
                            value = validatedValue ?: reminder.value,
                            timeUnit = timeUnit ?: reminder.timeUnit
                        )
                    } else {
                        reminder
                    }
                }
            }
        }
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
    fun toggleSoundSelection() {
        _userSoundSelection.value = _userSoundSelection.value.toggle()
    }
    fun setSelectedAudioFile(fileName: String) {
        _selectedAudioFile.value = fileName
    }
    fun toggleVibrateSelection() {
        _userVibrateSelection.value = _userVibrateSelection.value.toggle()
    }

    // Color
    fun toggleColorSelection() {
        _userColorSelection.value = _userColorSelection.value.toggle()
    }
    fun setSelectedColorChoice(color: UserColorChoices) {
        _selectedColor.value = color
    }

    // Misc
    fun clearAllInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        clearTitleInputSelection()
        setCalendarVisible(false)
    }
}
