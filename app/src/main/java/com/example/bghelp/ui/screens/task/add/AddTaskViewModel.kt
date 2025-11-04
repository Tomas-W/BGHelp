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
    private val _userTitleSelection = MutableStateFlow(UserTitleSelection.TITLE_ONLY)
    val userTitleSelection: StateFlow<UserTitleSelection> = _userTitleSelection.asStateFlow()
    
    // Title text
    private val _titleText = MutableStateFlow("")
    val titleText: StateFlow<String> = _titleText.asStateFlow()
    
    // Info text
    private val _infoText = MutableStateFlow("")
    val infoText: StateFlow<String> = _infoText.asStateFlow()
    
    // When selection
    private val _userDateSelection = MutableStateFlow(UserDateSelection.AT_TIME)
    val userDateSelection: StateFlow<UserDateSelection> = _userDateSelection.asStateFlow()

    // Date selection
    private val _dateStartSelection = MutableStateFlow(LocalDate.now())
    val dateStartSelection: StateFlow<LocalDate> = _dateStartSelection.asStateFlow()

    private val _dateEndSelection = MutableStateFlow<LocalDate?>(null)
    val dateEndSelection: StateFlow<LocalDate?> = _dateEndSelection.asStateFlow()

    // To save/load when toggling end date
    private var hiddenDateEndValue: LocalDate? = null

    // Time selection (for AT_TIME mode)
    private val _timeStartSelection = MutableStateFlow(LocalTime.now().withSecond(0).withNano(0))
    val timeStartSelection: StateFlow<LocalTime> = _timeStartSelection.asStateFlow()

    private val _timeEndSelection = MutableStateFlow<LocalTime?>(null)
    val timeEndSelection: StateFlow<LocalTime?> = _timeEndSelection.asStateFlow()

    // To save/load when toggling end time
    private var hiddenTimeEndValue: LocalTime? = null

    // Reminder selection
    private val _userRemindSelection = MutableStateFlow(UserRemindSelection.OFF)
    val userRemindSelection: StateFlow<UserRemindSelection> = _userRemindSelection.asStateFlow()

    // Sound selection
    private val _userSoundSelection = MutableStateFlow(UserSoundSelection.OFF)
    val userSoundSelection: StateFlow<UserSoundSelection> = _userSoundSelection.asStateFlow()

    // Vibrate selection
    private val _userVibrateSelection = MutableStateFlow(UserVibrateSelection.OFF)
    val userVibrateSelection: StateFlow<UserVibrateSelection> = _userVibrateSelection.asStateFlow()

    // Selected audio file
    private val _selectedAudioFile = MutableStateFlow<String>("")
    val selectedAudioFile: StateFlow<String> = _selectedAudioFile.asStateFlow()

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
    data class ActiveReminderInput(val type: RemindType, val id: Int)
    private val _activeReminderInput = MutableStateFlow<ActiveReminderInput?>(null)
    val activeReminderInput: StateFlow<ActiveReminderInput?> = _activeReminderInput.asStateFlow()
    
    // Active title input tracking
    enum class TitleInputType { TITLE, INFO }
    private val _activeTitleInput = MutableStateFlow<TitleInputType?>(null)
    val activeTitleInput: StateFlow<TitleInputType?> = _activeTitleInput.asStateFlow()

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
    fun toggleTitleSelection() {
        _userTitleSelection.value = if (_userTitleSelection.value == UserTitleSelection.TITLE_ONLY)
            UserTitleSelection.TITLE_AND_INFO else UserTitleSelection.TITLE_ONLY
    }
    
    fun setTitleText(text: String) {
        _titleText.value = text
    }
    
    fun setInfoText(text: String) {
        _infoText.value = text
    }
    
    fun toggleDateSelection() {
        _userDateSelection.value = if (_userDateSelection.value == UserDateSelection.AT_TIME)
            UserDateSelection.ALL_DAY else UserDateSelection.AT_TIME
    }

    fun toggleRemindSelection() {
        _userRemindSelection.value = if (_userRemindSelection.value == UserRemindSelection.OFF) UserRemindSelection.ON else UserRemindSelection.OFF
    }

    fun toggleSoundSelection() {
        val soundMap = mapOf(
            UserSoundSelection.OFF to UserSoundSelection.ONCE,
            UserSoundSelection.ONCE to UserSoundSelection.CONTINUOUS,
            UserSoundSelection.CONTINUOUS to UserSoundSelection.OFF
        )
        _userSoundSelection.value = soundMap[_userSoundSelection.value] ?: UserSoundSelection.OFF
    }

    fun toggleVibrateSelection() {
        val vibrateMap = mapOf(
            UserVibrateSelection.OFF to UserVibrateSelection.ONCE,
            UserVibrateSelection.ONCE to UserVibrateSelection.CONTINUOUS,
            UserVibrateSelection.CONTINUOUS to UserVibrateSelection.OFF
        )
        _userVibrateSelection.value = vibrateMap[_userVibrateSelection.value] ?: UserVibrateSelection.OFF
    }

    fun setSelectedAudioFile(fileName: String) {
        _selectedAudioFile.value = fileName
    }

    fun setDateStart(date: LocalDate) {
        _dateStartSelection.value = date
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

    fun setTimeEnd(time: LocalTime) {
        _timeEndSelection.value = time
        hiddenTimeEndValue = time
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
            _dateEndSelection.value = hiddenDateEndValue ?: _dateStartSelection.value
        }
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
        clearTitleInputSelection()
    }

    fun setActiveTimeInput(field: TimeField?, segment: TimeSegment?) {
        _activeTimeField.value = field
        _activeTimeSegment.value = segment
        if (field != null) {
            clearReminderInputSelection()
            clearTitleInputSelection()
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

    fun clearAllInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        clearTitleInputSelection()
        setCalendarVisible(false)
    }
}
