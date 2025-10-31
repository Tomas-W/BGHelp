package com.example.bghelp.ui.screens.task.add

import androidx.lifecycle.ViewModel
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
class AddTaskViewModel @Inject constructor() : ViewModel() {
    
    // When selection
    private val _userDateSelection = MutableStateFlow(UserDateSelection.AT_TIME)
    val userDateSelection: StateFlow<UserDateSelection> = _userDateSelection.asStateFlow()

    // Date selection
    private val _dateStartSelection = MutableStateFlow(LocalDate.now())
    val dateStartSelection: StateFlow<LocalDate> = _dateStartSelection.asStateFlow()

    private val _dateEndSelection = MutableStateFlow<LocalDate?>(null)
    val dateEndSelection: StateFlow<LocalDate?> = _dateEndSelection.asStateFlow()

    // Time selection (for AT_TIME mode)
    private val _timeStartSelection = MutableStateFlow(LocalTime.now().withSecond(0).withNano(0))
    val timeStartSelection: StateFlow<LocalTime> = _timeStartSelection.asStateFlow()

    private val _timeEndSelection = MutableStateFlow<LocalTime?>(null)
    val timeEndSelection: StateFlow<LocalTime?> = _timeEndSelection.asStateFlow()

    // Reminder selection
    private val _userNotifySelection = MutableStateFlow(UserNotifySelection.OFF)
    val userNotifySelection: StateFlow<UserNotifySelection> = _userNotifySelection.asStateFlow()

    // dateEnd & timeEnd visibility
    private val _isEndDateVisible = MutableStateFlow(false)
    val isEndDateVisible: StateFlow<Boolean> = _isEndDateVisible.asStateFlow()

    private val _isEndTimeVisible = MutableStateFlow(false)
    val isEndTimeVisible: StateFlow<Boolean> = _isEndTimeVisible.asStateFlow()

    // Calendar state
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _activeDateField = MutableStateFlow<DateField?>(null)
    val activeDateField: StateFlow<DateField?> = _activeDateField.asStateFlow()

    private val _isCalendarVisible = MutableStateFlow(false)
    val isCalendarVisible: StateFlow<Boolean> = _isCalendarVisible.asStateFlow()

    // Keyboard dismiss tracking
    private val _keyboardDismissKey = MutableStateFlow(0)
    val keyboardDismissKey: StateFlow<Int> = _keyboardDismissKey.asStateFlow()

    // Time input selection tracking
    private val _activeTimeField = MutableStateFlow<TimeField?>(null)
    val activeTimeField: StateFlow<TimeField?> = _activeTimeField.asStateFlow()

    private val _activeTimeSegment = MutableStateFlow<TimeSegment?>(null)
    val activeTimeSegment: StateFlow<TimeSegment?> = _activeTimeSegment.asStateFlow()

    // Time validation
    private val _isTimeRangeInvalid = MutableStateFlow(false)
    val isTimeRangeInvalid: StateFlow<Boolean> = _isTimeRangeInvalid.asStateFlow()

    // Reminders
    private var nextReminderId = 0
    private val _startReminders = MutableStateFlow<List<Reminder>>(emptyList())
    val startReminders: StateFlow<List<Reminder>> = _startReminders.asStateFlow()

    private val _endReminders = MutableStateFlow<List<Reminder>>(emptyList())
    val endReminders: StateFlow<List<Reminder>> = _endReminders.asStateFlow()

    // Active reminder input tracking
    data class ActiveReminderInput(val type: ReminderType, val id: Int)
    private val _activeReminderInput = MutableStateFlow<ActiveReminderInput?>(null)
    val activeReminderInput: StateFlow<ActiveReminderInput?> = _activeReminderInput.asStateFlow()

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

    // Actions
    fun toggleWhenSelection() {
        _userDateSelection.value = if (_userDateSelection.value == UserDateSelection.AT_TIME)
            UserDateSelection.ALL_DAY else UserDateSelection.AT_TIME
    }

    fun toggleNotifySelection() {
        _userNotifySelection.value = if (_userNotifySelection.value == UserNotifySelection.OFF) UserNotifySelection.ON else UserNotifySelection.OFF
    }

    fun setDateStart(date: LocalDate) {
        _dateStartSelection.value = date
        _dateEndSelection.value = _dateEndSelection.value?.let { maxOf(it, date) }
        updateTimeValidationState()
    }

    fun setDateEnd(date: LocalDate) {
        _dateEndSelection.value = date
        _dateStartSelection.value = _dateStartSelection.value.coerceAtMost(date)
        updateTimeValidationState()
    }

    fun setTimeStart(time: LocalTime) {
        _timeStartSelection.value = time
        updateTimeValidationState()
    }

    fun setTimeEnd(time: LocalTime) {
        _timeEndSelection.value = time
        updateTimeValidationState()
    }

    fun toggleEndDateVisible() {
        if (_isEndDateVisible.value) {
            _isEndDateVisible.value = false
            _dateEndSelection.value = null
            if (_activeDateField.value == DateField.END) {
                setCalendarVisible(false)
            }
        } else {
            _isEndDateVisible.value = true
            _dateEndSelection.value = _dateStartSelection.value
        }
        updateTimeValidationState()
    }

    fun toggleEndTimeVisible() {
        if (_isEndTimeVisible.value) {
            _isEndTimeVisible.value = false
            _timeEndSelection.value = null
        } else {
            _isEndTimeVisible.value = true
            _timeEndSelection.value = _timeStartSelection.value.plusHours(1)
        }
        updateTimeValidationState()
    }

    fun setCurrentMonth(month: YearMonth) {
        _currentMonth.value = month
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

    fun onTimeInputClicked() {
        setCalendarVisible(false)
        clearReminderInputSelection()
    }

    fun setActiveTimeInput(field: TimeField?, segment: TimeSegment?) {
        _activeTimeField.value = field
        _activeTimeSegment.value = segment
        if (field != null) {
            clearReminderInputSelection()
        }
    }

    fun clearTimeInputSelection() {
        _activeTimeField.value = null
        _activeTimeSegment.value = null
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

    // Reminder actions
    fun addReminder(type: ReminderType) {
        val newReminder = Reminder(
            id = nextReminderId++,
            value = 1,
            timeUnit = TimeUnit.MINUTES
        )
        when (type) {
            ReminderType.START -> {
                _startReminders.value = _startReminders.value + newReminder
            }
            ReminderType.END -> {
                _endReminders.value = _endReminders.value + newReminder
            }
        }
    }

    fun removeReminder(type: ReminderType, reminderId: Int) {
        when (type) {
            ReminderType.START -> {
                _startReminders.value = _startReminders.value.filter { it.id != reminderId }
            }
            ReminderType.END -> {
                _endReminders.value = _endReminders.value.filter { it.id != reminderId }
            }
        }
    }

    fun updateReminder(type: ReminderType, reminderId: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        when (type) {
            ReminderType.START -> {
                _startReminders.value = _startReminders.value.map { reminder ->
                    if (reminder.id == reminderId) {
                        reminder.copy(
                            value = value ?: reminder.value,
                            timeUnit = timeUnit ?: reminder.timeUnit
                        )
                    } else {
                        reminder
                    }
                }
            }
            ReminderType.END -> {
                _endReminders.value = _endReminders.value.map { reminder ->
                    if (reminder.id == reminderId) {
                        reminder.copy(
                            value = value ?: reminder.value,
                            timeUnit = timeUnit ?: reminder.timeUnit
                        )
                    } else {
                        reminder
                    }
                }
            }
        }
    }

    fun setActiveReminderInput(type: ReminderType, id: Int) {
        _activeReminderInput.value = ActiveReminderInput(type, id)
        setCalendarVisible(false)
        clearTimeInputSelection()
        _keyboardDismissKey.value++
    }

    fun clearReminderInputSelection() {
        _activeReminderInput.value = null
    }

    fun clearAllInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        setCalendarVisible(false)
    }
}
