package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.TaskColorOption
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.domain.model.TaskImageSourceOption
import com.example.bghelp.domain.model.TaskLocationEntry
import com.example.bghelp.domain.model.TaskReminderEntry
import com.example.bghelp.utils.AudioManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject
import java.util.Locale
import kotlin.random.Random

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    val audioManager: AudioManager,
    private val taskRepository: TaskRepository
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
    // Monthly
    private val _monthlySelectedMonths = MutableStateFlow((1..12).toSet())
    val monthlySelectedMonths: StateFlow<Set<Int>> = _monthlySelectedMonths.asStateFlow()
    private val _userMonthlyDaySelection = MutableStateFlow(RepeatMonthlyDaySelection.ALL)
    val userMonthlyDaySelection: StateFlow<RepeatMonthlyDaySelection> = _userMonthlyDaySelection.asStateFlow()
    private val _monthlySelectedDays = MutableStateFlow((1..31).toSet())
    val monthlySelectedDays: StateFlow<Set<Int>> = _monthlySelectedDays.asStateFlow()
    private val _allMonthDaysSelected = MutableStateFlow(true)
    val allMonthDaysSelected: StateFlow<Boolean> = _allMonthDaysSelected.asStateFlow()

    private val _repeatRRule = MutableStateFlow<String?>(null)
    val repeatRRule: StateFlow<String?> = _repeatRRule.asStateFlow()

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
    
    // Note selection
    private val _userNoteSelection = MutableStateFlow(UserNoteSelection.OFF)
    val userNoteSelection: StateFlow<UserNoteSelection> = _userNoteSelection.asStateFlow()
    private val _selectedNote = MutableStateFlow("")
    val selectedNote: StateFlow<String> = _selectedNote.asStateFlow()
    // Vibrate selection
    private val _userVibrateSelection = MutableStateFlow(UserVibrateSelection.OFF)
    val userVibrateSelection: StateFlow<UserVibrateSelection> = _userVibrateSelection.asStateFlow()

    // Location
    private val _userLocationSelection = MutableStateFlow(UserLocationSelection.OFF)
    val userLocationSelection: StateFlow<UserLocationSelection> = _userLocationSelection.asStateFlow()
    private val _selectedLocations = MutableStateFlow<List<TaskLocation>>(emptyList())
    val selectedLocations: StateFlow<List<TaskLocation>> = _selectedLocations.asStateFlow()

    // Image
    private val _userImageSelection = MutableStateFlow(UserImageSelection.OFF)
    val userImageSelection: StateFlow<UserImageSelection> = _userImageSelection.asStateFlow()
    private val _selectedImage = MutableStateFlow<TaskImageData?>(null)
    val selectedImage: StateFlow<TaskImageData?> = _selectedImage.asStateFlow()

    // Color
    private val _userColorSelection = MutableStateFlow(UserColorSelection.OFF)
    val userColorSelection: StateFlow<UserColorSelection> = _userColorSelection.asStateFlow()
    private val _selectedColor = MutableStateFlow(UserColorChoices.DEFAULT)
    val selectedColor: StateFlow<UserColorChoices> = _selectedColor.asStateFlow()

    // Keyboard dismiss tracking
    private val _keyboardDismissKey = MutableStateFlow(0)
    val keyboardDismissKey: StateFlow<Int> = _keyboardDismissKey.asStateFlow()

    private val _saveState = MutableStateFlow<SaveTaskState>(SaveTaskState.Idle)
    val saveState: StateFlow<SaveTaskState> = _saveState.asStateFlow()

    init {
        updateTimeValidationState()
        refreshRepeatRule()
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
        refreshRepeatRule()
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
        refreshRepeatRule()
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
        refreshRepeatRule()
    }
    fun toggleWeeklySelectedDays(day: Int) {
        val currentDays = _weeklySelectedDays.value.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _weeklySelectedDays.value = currentDays
        refreshRepeatRule()
    }
    fun setWeeklyIntervalWeeks(weeks: Int) {
        _weeklyIntervalWeeks.value = weeks
        refreshRepeatRule()
    }
    fun toggleMonthlySelectedMonths(month: Int) {
        val currentMonths = _monthlySelectedMonths.value.toMutableSet()
        if (currentMonths.contains(month)) {
            currentMonths.remove(month)
        } else {
            currentMonths.add(month)
        }
        _monthlySelectedMonths.value = currentMonths
        refreshRepeatRule()
    }
    fun toggleMonthSelectedDays(day: Int) {
        val currentDays = _monthlySelectedDays.value.toMutableSet()
        if (currentDays.contains(day)) {
            currentDays.remove(day)
        } else {
            currentDays.add(day)
        }
        _monthlySelectedDays.value = currentDays
        _allMonthDaysSelected.value = currentDays.size == 31
        refreshRepeatRule()
    }
    fun selectAllMonthlySelectedMonths() {
        _monthlySelectedMonths.value = (1..12).toSet()
        refreshRepeatRule()
    }
    fun deselectAllMonthlySelectedMonths() {
        _monthlySelectedMonths.value = emptySet()
        refreshRepeatRule()
    }
    fun toggleMonthlyDaySelection(newSelection: RepeatMonthlyDaySelection) {
        _userMonthlyDaySelection.value = newSelection
        refreshRepeatRule()
    }
    fun selectAllMonthlySelectedDays() {
        _monthlySelectedDays.value = (1..31).toSet()
        _allMonthDaysSelected.value = true
        refreshRepeatRule()
    }
    fun deselectAllMonthlySelectedDays() {
        _monthlySelectedDays.value = emptySet()
        _allMonthDaysSelected.value = false
        refreshRepeatRule()
    }

    private fun buildRepeatRRule(
        selection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        monthlyMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        startDate: LocalDate
    ): String? {
        return when (selection) {
            UserRepeatSelection.OFF -> null
            UserRepeatSelection.WEEKLY -> buildWeeklyRRule(
                weeklyDays = weeklyDays,
                weeklyInterval = weeklyInterval,
                startDate = startDate
            )
            UserRepeatSelection.MONTHLY -> buildMonthlyRRule(
                selectedMonths = monthlyMonths,
                monthlyDaySelection = monthlyDaySelection,
                selectedDays = monthlyDays,
                startDate = startDate
            )
        }
    }

    private fun buildWeeklyRRule(
        weeklyDays: Set<Int>,
        weeklyInterval: Int,
        startDate: LocalDate
    ): String {
        val normalizedDays = weeklyDays.mapNotNull { it.toDayOfWeekOrNull() }
        val effectiveDays = if (normalizedDays.isEmpty()) {
            listOf(startDate.dayOfWeek)
        } else {
            normalizedDays
        }
        val dayTokens = effectiveDays
            .distinct()
            .sortedBy { it.value }
            .joinToString(",") { it.toRRuleToken() }

        return buildString {
            append("FREQ=WEEKLY")
            if (weeklyInterval > 1) {
                append(";INTERVAL=$weeklyInterval")
            }
            append(";BYDAY=$dayTokens")
        }
    }

    private fun buildMonthlyRRule(
        selectedMonths: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        selectedDays: Set<Int>,
        startDate: LocalDate
    ): String {
        val months = selectedMonths
            .filter { it in 1..12 }
            .distinct()
            .sorted()
            .let {
                when {
                    it.isEmpty() -> listOf(startDate.monthValue)
                    it.size == 12 -> emptyList()
                    else -> it
                }
            }

        val days = when (monthlyDaySelection) {
            RepeatMonthlyDaySelection.ALL -> (1..31).toList()
            RepeatMonthlyDaySelection.SELECT -> {
                val normalized = selectedDays.filter { it in 1..31 }.distinct().sorted()
                if (normalized.isEmpty()) listOf(startDate.dayOfMonth) else normalized
            }
            RepeatMonthlyDaySelection.LAST -> listOf(-1)
        }

        return buildString {
            append("FREQ=MONTHLY")
            if (months.isNotEmpty()) {
                append(";BYMONTH=${months.joinToString(",")}")
            }
            if (days.isNotEmpty()) {
                append(";BYMONTHDAY=${days.joinToString(",")}")
            }
        }
    }

    private fun Int.toDayOfWeekOrNull(): DayOfWeek? = runCatching {
        DayOfWeek.of(this)
    }.getOrNull()

    private fun DayOfWeek.toRRuleToken(): String = when (this) {
        DayOfWeek.MONDAY -> "MO"
        DayOfWeek.TUESDAY -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY -> "TH"
        DayOfWeek.FRIDAY -> "FR"
        DayOfWeek.SATURDAY -> "SA"
        DayOfWeek.SUNDAY -> "SU"
    }

    private fun refreshRepeatRule() {
        _repeatRRule.value = buildRepeatRRule(
            selection = _userRepeatSelection.value,
            weeklyDays = _weeklySelectedDays.value,
            weeklyInterval = _weeklyIntervalWeeks.value,
            monthlyMonths = _monthlySelectedMonths.value,
            monthlyDaySelection = _userMonthlyDaySelection.value,
            monthlyDays = _monthlySelectedDays.value,
            startDate = _dateStartSelection.value
        )
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
    fun toggleNoteSelection() {
        _userNoteSelection.value = _userNoteSelection.value.toggle()
        if (_userNoteSelection.value == UserNoteSelection.OFF) {
            _selectedNote.value = ""
        }
    }
    fun setSelectedNote(note: String) {
        _selectedNote.value = note
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

    // Location
    fun toggleLocationSelection() {
        _userLocationSelection.value = _userLocationSelection.value.toggle()
    }
    fun generateRandomLocation() {
        val latitude = Random.nextDouble(from = -90.0, until = 90.0)
        val longitude = Random.nextDouble(from = -180.0, until = 180.0)
        val addressSuffix = Random.nextInt(1000, 9999)
        val address = String.format(
            Locale.getDefault(),
            "Random Address %04d",
            addressSuffix
        )

        setSelectedLocation(
            TaskLocation(
                latitude = latitude,
                longitude = longitude,
                address = address,
                name = address
            )
        )
    }

    fun setSelectedLocation(location: TaskLocation) {
        setSelectedLocations(listOf(location))
    }

    fun setSelectedLocations(locations: List<TaskLocation>) {
        _selectedLocations.value = locations
        _userLocationSelection.value =
            if (locations.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
    }

    fun appendSelectedLocations(locations: List<TaskLocation>) {
        if (locations.isEmpty()) return
        val merged = (_selectedLocations.value + locations).distinctBy { location ->
            location.latitude to location.longitude
        }
        _selectedLocations.value = merged
        _userLocationSelection.value =
            if (merged.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
    }

    fun removeSelectedLocation(location: TaskLocation) {
        val updatedLocations = _selectedLocations.value.filterNot { it == location }
        _selectedLocations.value = updatedLocations
        _userLocationSelection.value =
            if (updatedLocations.isEmpty()) UserLocationSelection.OFF else UserLocationSelection.ON
    }

    // Image
    fun toggleImageSelection() {
        _userImageSelection.value = _userImageSelection.value.toggle()
    }
    fun setImageFromGallery(uri: Uri, displayName: String?) {
        val label = displayName?.takeIf { it.isNotBlank() } ?: AddTaskStrings.NO_IMAGE_SELECTED
        _selectedImage.value = TaskImageData(
            displayName = label,
            uri = uri,
            bitmap = null,
            source = TaskImageSource.GALLERY
        )
    }
    fun setImageFromCamera(bitmap: Bitmap, displayName: String = AddTaskStrings.CAPTURED_IMAGE) {
        _selectedImage.value = TaskImageData(
            displayName = displayName,
            uri = null,
            bitmap = bitmap,
            source = TaskImageSource.CAMERA
        )
    }
    fun clearSelectedImage() {
        _selectedImage.value = null
    }

    // Misc
    fun clearAllInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        clearTitleInputSelection()
        setCalendarVisible(false)
    }

    fun saveTask() {
        if (_saveState.value == SaveTaskState.Saving) return
        viewModelScope.launch {
            _saveState.value = SaveTaskState.Saving
            try {
                val task = buildCreateTask()
                taskRepository.addTask(task)
                _saveState.value = SaveTaskState.Success
            } catch (t: Throwable) {
                _saveState.value = SaveTaskState.Error(t)
            }
        }
    }

    fun consumeSaveState() {
        _saveState.value = SaveTaskState.Idle
    }

    private fun buildCreateTask(): CreateTask {
        val allDay = _userDateSelection.value == UserDateSelection.ON
        val startDate = _dateStartSelection.value
        val startTime = if (allDay) LocalTime.MIDNIGHT else _timeStartSelection.value
        val startDateTime = LocalDateTime.of(startDate, startTime)

        val hasEnd = _isEndDateVisible.value || _isEndTimeVisible.value
        val endDate = if (_isEndDateVisible.value) _dateEndSelection.value else null
        val endTime = if (_isEndTimeVisible.value) _timeEndSelection.value else null
        val effectiveEndDate = when {
            hasEnd -> endDate ?: startDate
            else -> null
        }
        val effectiveEndTime = when {
            !hasEnd -> null
            endTime != null -> endTime
            allDay -> LocalTime.of(23, 59)
            else -> _timeStartSelection.value
        }
        val endDateTime = if (effectiveEndDate != null && effectiveEndTime != null) {
            LocalDateTime.of(effectiveEndDate, effectiveEndTime)
        } else {
            null
        }

        val reminders = if (_userRemindSelection.value == UserRemindSelection.ON) {
            buildReminders()
        } else {
            emptyList()
        }

        val image = _selectedImage.value?.toDomainImage()
        val locations = buildLocations()

        return CreateTask(
            date = startDateTime,
            endDate = endDateTime,
            allDay = allDay,
            title = _titleText.value,
            description = _infoText.value.takeIf { it.isNotBlank() },
            note = _selectedNote.value.takeIf { it.isNotBlank() },
            rrule = _repeatRRule.value,
            expired = false,
            alarmName = _selectedAudioFile.value.takeIf { it.isNotBlank() },
            sound = _userSoundSelection.value.toAlarmMode(),
            vibrate = _userVibrateSelection.value.toAlarmMode(),
            soundUri = _selectedAudioFile.value.takeIf { it.isNotBlank() },
            snoozeTime = 0,
            color = _selectedColor.value.toDomainColor(),
            image = image,
            reminders = reminders,
            locations = locations
        )
    }

    private fun buildReminders(): List<TaskReminderEntry> {
        val start = _startReminders.value.mapNotNull { it.toDomain(ReminderKind.START) }
        val end = _endReminders.value.mapNotNull { it.toDomain(ReminderKind.END) }
        return start + end
    }

    private fun Reminder.toDomain(type: ReminderKind): TaskReminderEntry? {
        val unit = timeUnit.toDomainUnit() ?: return null
        return TaskReminderEntry(
            type = type,
            offsetValue = value,
            offsetUnit = unit
        )
    }

    private fun TimeUnit.toDomainUnit(): ReminderOffsetUnit? = when (this) {
        TimeUnit.MINUTES -> ReminderOffsetUnit.MINUTES
        TimeUnit.HOURS -> ReminderOffsetUnit.HOURS
        TimeUnit.DAYS -> ReminderOffsetUnit.DAYS
        TimeUnit.WEEKS -> ReminderOffsetUnit.WEEKS
        TimeUnit.MONTHS -> ReminderOffsetUnit.MONTHS
    }

    private fun UserSoundSelection.toAlarmMode(): AlarmMode = when (this) {
        UserSoundSelection.OFF -> AlarmMode.OFF
        UserSoundSelection.ONCE -> AlarmMode.ONCE
        UserSoundSelection.CONTINUOUS -> AlarmMode.CONTINUOUS
    }

    private fun UserVibrateSelection.toAlarmMode(): AlarmMode = when (this) {
        UserVibrateSelection.OFF -> AlarmMode.OFF
        UserVibrateSelection.ONCE -> AlarmMode.ONCE
        UserVibrateSelection.CONTINUOUS -> AlarmMode.CONTINUOUS
    }

    private fun UserColorChoices.toDomainColor(): TaskColorOption = when (this) {
        UserColorChoices.DEFAULT -> TaskColorOption.DEFAULT
        UserColorChoices.RED -> TaskColorOption.RED
        UserColorChoices.GREEN -> TaskColorOption.GREEN
        UserColorChoices.YELLOW -> TaskColorOption.YELLOW
        UserColorChoices.CYAN -> TaskColorOption.CYAN
        UserColorChoices.MAGENTA -> TaskColorOption.MAGENTA
    }

    private fun TaskImageData.toDomainImage(): TaskImageAttachment? {
        val source = source.toDomainSource() ?: return null
        return TaskImageAttachment(
            uri = uri?.toString(),
            displayName = displayName.takeIf { it.isNotBlank() },
            source = source
        )
    }

    private fun TaskImageSource.toDomainSource(): TaskImageSourceOption? = when (this) {
        TaskImageSource.GALLERY -> TaskImageSourceOption.GALLERY
        TaskImageSource.CAMERA -> TaskImageSourceOption.CAMERA
    }

    private fun buildLocations(): List<TaskLocationEntry> =
        _selectedLocations.value.mapIndexed { index, location ->
            TaskLocationEntry(
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address,
                name = location.name,
                order = index
            )
        }

    sealed interface SaveTaskState {
        data object Idle : SaveTaskState
        data object Saving : SaveTaskState
        data object Success : SaveTaskState
        data class Error(val throwable: Throwable) : SaveTaskState
    }
}
