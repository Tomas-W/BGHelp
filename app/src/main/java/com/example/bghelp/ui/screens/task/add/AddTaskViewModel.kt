package com.example.bghelp.ui.screens.task.add

import android.graphics.Bitmap
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.ColorRepository
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.utils.AudioManager
import com.example.bghelp.utils.RepeatRuleBuilder
import com.example.bghelp.utils.TaskMapper
import com.example.bghelp.ui.theme.TaskDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random
import com.example.bghelp.utils.TaskImageStorage

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    val audioManager: AudioManager,
    private val taskRepository: TaskRepository,
    private val colorRepository: ColorRepository,
    @ApplicationContext private val appContext: Context
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
    private val _dateStartSelection = MutableStateFlow(LocalDate.now().plusDays(1))
    val dateStartSelection: StateFlow<LocalDate> = _dateStartSelection.asStateFlow()
    private val _dateEndSelection = MutableStateFlow<LocalDate?>(null)
    val dateEndSelection: StateFlow<LocalDate?> = _dateEndSelection.asStateFlow()
    private val _isEndDateVisible = MutableStateFlow(false)
    val isEndDateVisible: StateFlow<Boolean> = _isEndDateVisible.asStateFlow()
    // Time
    private val _timeStartSelection = MutableStateFlow(LocalTime.of(12, 0))
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
    // RRULE
    private val _repeatRRule = MutableStateFlow<String?>(null)

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
    private val _selectedColorId = MutableStateFlow<Int?>(null)
    val selectedColorId: StateFlow<Int?> = _selectedColorId.asStateFlow()
    val allColors: StateFlow<List<FeatureColor>> = colorRepository.getAllColors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Keyboard dismiss tracking
    private val _keyboardDismissKey = MutableStateFlow(0)
    val keyboardDismissKey: StateFlow<Int> = _keyboardDismissKey.asStateFlow()

    // Saving a task
    private val _saveState = MutableStateFlow<SaveTaskState>(SaveTaskState.Idle)
    val saveState: StateFlow<SaveTaskState> = _saveState.asStateFlow()

    // Snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    // Validation - Separate StateFlows for each category
    val isTitleValid: StateFlow<Boolean> = _titleText.map { title ->
        AddTaskValidator.validateTitle(title) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    val isDateTimeValid: StateFlow<Boolean> = combine(
        _userDateSelection,
        _dateStartSelection,
        _timeStartSelection,
        _dateEndSelection,
        _isEndDateVisible,
        _timeEndSelection,
        _isEndTimeVisible
    ) { values: Array<Any?> ->
        val dateSelection = values[0] as UserDateSelection
        val startDate = values[1] as LocalDate
        val startTime = values[2] as LocalTime
        val endDate = values[3] as LocalDate?
        val isEndDateVisible = values[4] as Boolean
        val endTime = values[5] as LocalTime?
        val isEndTimeVisible = values[6] as Boolean
        AddTaskValidator.validateDateTime(
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        ) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    val isRepeatValid: StateFlow<Boolean> = combine(
        _userRepeatSelection,
        _weeklySelectedDays,
        _userMonthlyDaySelection,
        _monthlySelectedDays
    ) { repeatSelection, weeklyDays, monthlyDaySelection, monthlyDays ->
        AddTaskValidator.validateRepeat(
            repeatSelection = repeatSelection,
            weeklyDays = weeklyDays,
            monthlyDaySelection = monthlyDaySelection,
            monthlyDays = monthlyDays
        ) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    val isRemindersValid: StateFlow<Boolean> = combine(
        _userRemindSelection,
        _userDateSelection,
        _dateStartSelection,
        _timeStartSelection,
        _startReminders,
        _endReminders,
        _dateEndSelection,
        _isEndDateVisible,
        _timeEndSelection,
        _isEndTimeVisible
    ) { values: Array<Any?> ->
        val remindSelection = values[0] as UserRemindSelection
        val dateSelection = values[1] as UserDateSelection
        val startDate = values[2] as LocalDate
        val startTime = values[3] as LocalTime
        @Suppress("UNCHECKED_CAST")
        val startReminders = values[4] as List<Reminder>
        @Suppress("UNCHECKED_CAST")
        val endReminders = values[5] as List<Reminder>
        val endDate = values[6] as LocalDate?
        val isEndDateVisible = values[7] as Boolean
        val endTime = values[8] as LocalTime?
        val isEndTimeVisible = values[9] as Boolean
        AddTaskValidator.validateReminders(
            remindSelection = remindSelection,
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            startReminders = startReminders,
            endReminders = endReminders,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        ) == null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
    
    // Combined validation
    val isNewTaskValid: StateFlow<Boolean> = combine(
        isTitleValid,
        isDateTimeValid,
        isRepeatValid,
        isRemindersValid
    ) { titleValid, dateTimeValid, repeatValid, remindersValid ->
        titleValid && dateTimeValid && repeatValid && remindersValid
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        refreshRepeatRule()
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
            val endDate = _dateEndSelection.value
            if (date.isAfter(endDate)) {
                _dateEndSelection.value = date
                hiddenDateEndValue = date
            }
        }
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
        refreshRepeatRule()
    }
    fun setDateEnd(date: LocalDate) {
        _dateEndSelection.value = date
        hiddenDateEndValue = date
        _dateStartSelection.value = _dateStartSelection.value.coerceAtMost(date)
        refreshRepeatRule()
    }
    fun setTimeStart(time: LocalTime) {
        _timeStartSelection.value = time
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
            _timeEndSelection.value = hiddenTimeEndValue ?: LocalTime.of(13, 0)
        }
    }
    fun setTimeEnd(time: LocalTime) {
        _timeEndSelection.value = time
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
        val newValue = _userRepeatSelection.value.toggle()
        _userRepeatSelection.value = newValue
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
    // RRULE
    private fun refreshRepeatRule() {
        _repeatRRule.value = RepeatRuleBuilder.buildRRule(
            selection = _userRepeatSelection.value,
            weeklyDays = _weeklySelectedDays.value,
            weeklyInterval = _weeklyIntervalWeeks.value,
            monthlyMonths = _monthlySelectedMonths.value,
            monthlyDaySelection = _userMonthlyDaySelection.value,
            monthlyDays = _monthlySelectedDays.value,
            startDate = _dateStartSelection.value,
            endDate = if (_isEndDateVisible.value) _dateEndSelection.value else null
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
    fun updateReminder(reminderType: RemindType, reminderId: Int, value: Int? = null, timeUnit: TimeUnit? = null) {
        val validatedValue = value?.coerceIn(AddTaskConstants.MIN_REMINDER, AddTaskConstants.MAX_REMINDER)

        when (reminderType) {
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
    fun setSelectedColorId(colorId: Int) {
        _selectedColorId.value = colorId
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
    fun clearNonCalendarInputSelections() {
        clearTimeInputSelection()
        clearReminderInputSelection()
        clearTitleInputSelection()
    }

    fun clearAllInputSelections() {
        clearNonCalendarInputSelections()
        setCalendarVisible(false)
    }

    fun saveTask() {
        if (_saveState.value == SaveTaskState.Saving) return
        
        // Validate and show first error if any
        val validationError = validateTask(
            title = _titleText.value,
            dateSelection = _userDateSelection.value,
            startDate = _dateStartSelection.value,
            startTime = _timeStartSelection.value,
            repeatSelection = _userRepeatSelection.value,
            weeklyDays = _weeklySelectedDays.value,
            monthlyDaySelection = _userMonthlyDaySelection.value,
            monthlyDays = _monthlySelectedDays.value,
            remindSelection = _userRemindSelection.value,
            startReminders = _startReminders.value,
            endReminders = _endReminders.value,
            endDate = _dateEndSelection.value,
            isEndDateVisible = _isEndDateVisible.value,
            endTime = _timeEndSelection.value,
            isEndTimeVisible = _isEndTimeVisible.value
        )
        
        if (validationError != null) {
            showSnackbar(validationError)
            return
        }
        
        _saveState.value = SaveTaskState.Saving
        viewModelScope.launch {
            try {
                val imageAttachment = TaskImageStorage.persistIfNeeded(
                    context = appContext,
                    isOn = _userImageSelection.value == UserImageSelection.ON,
                    selected = _selectedImage.value
                )
                val allDay = _userDateSelection.value == UserDateSelection.ON
                val startDate = _dateStartSelection.value
                val startTime = if (allDay) LocalTime.MIDNIGHT else _timeStartSelection.value
                val endDateVisible = _isEndDateVisible.value
                val endTimeVisible = _isEndTimeVisible.value
                val endDate = if (endDateVisible) _dateEndSelection.value else null
                val endTime = if (endTimeVisible) _timeEndSelection.value else null
                val assembleResult = AddTaskAssembler.buildOrError(
                    title = _titleText.value,
                    dateSelection = _userDateSelection.value,
                    startDate = startDate,
                    startTime = startTime,
                    repeatSelection = _userRepeatSelection.value,
                    weeklyDays = _weeklySelectedDays.value,
                    monthlyDaySelection = _userMonthlyDaySelection.value,
                    monthlyDays = _monthlySelectedDays.value,
                    remindSelection = _userRemindSelection.value,
                    startReminders = _startReminders.value,
                    endReminders = _endReminders.value,
                    endDate = endDate,
                    isEndDateVisible = endDateVisible,
                    endTime = endTime,
                    isEndTimeVisible = endTimeVisible,
                    rrule = _repeatRRule.value,
                    alarmName = _selectedAudioFile.value.takeIf { it.isNotBlank() },
                    soundMode = with(TaskMapper) { _userSoundSelection.value.toAlarmMode() },
                    vibrateMode = with(TaskMapper) { _userVibrateSelection.value.toAlarmMode() },
                    soundUri = _selectedAudioFile.value.takeIf { it.isNotBlank() },
                    color = getSelectedTaskColor(),
                    image = imageAttachment,
                    description = _infoText.value.takeIf { it.isNotBlank() },
                    note = if (_userNoteSelection.value == UserNoteSelection.ON)
                        _selectedNote.value.takeIf { it.isNotBlank() } else null,
                    locations = _selectedLocations.value
                )

                when (assembleResult) {
                    is AddTaskAssembler.BuildResult.Error -> {
                        showSnackbar(assembleResult.message)
                        _saveState.value = SaveTaskState.Idle
                        return@launch
                    }
                    is AddTaskAssembler.BuildResult.Success -> {
                        taskRepository.addTask(assembleResult.task)
                        _saveState.value = SaveTaskState.Success
                    }
                }
            } catch (t: Throwable) {
                _saveState.value = SaveTaskState.Error(t)
            }
        }
    }

    fun consumeSaveState() {
        _saveState.value = SaveTaskState.Idle
    }

    fun resetAllData() {
        // Title
        _userTitleSelection.value = UserTitleSelection.OFF
        _titleText.value = ""
        _infoText.value = ""
        _activeTitleInput.value = null

        // Date/Time
        _userDateSelection.value = UserDateSelection.OFF
        _dateStartSelection.value = LocalDate.now().plusDays(1)
        _dateEndSelection.value = null
        _isEndDateVisible.value = false
        _timeStartSelection.value = LocalTime.of(12, 0)
        _timeEndSelection.value = null
        _isEndTimeVisible.value = false
        hiddenDateEndValue = null
        hiddenTimeEndValue = null
        _isCalendarVisible.value = false
        _currentMonth.value = YearMonth.now()
        _activeDateField.value = null
        _activeTimeSegment.value = null
        _activeTimeField.value = null

        // Repeat
        _userRepeatSelection.value = UserRepeatSelection.OFF
        _weeklySelectedDays.value = setOf(1, 2, 3, 4, 5, 6, 7)
        _weeklyIntervalWeeks.value = 1
        _monthlySelectedMonths.value = (1..12).toSet()
        _userMonthlyDaySelection.value = RepeatMonthlyDaySelection.ALL
        _monthlySelectedDays.value = (1..31).toSet()
        _allMonthDaysSelected.value = true
        _repeatRRule.value = null

        // Remind
        _userRemindSelection.value = UserRemindSelection.OFF
        _startReminders.value = emptyList()
        _endReminders.value = emptyList()
        nextReminderId = 0
        _activeReminderInput.value = null
        _userSoundSelection.value = UserSoundSelection.OFF
        _selectedAudioFile.value = ""
        _userVibrateSelection.value = UserVibrateSelection.OFF

        // Note
        _userNoteSelection.value = UserNoteSelection.OFF
        _selectedNote.value = ""

        // Location
        _userLocationSelection.value = UserLocationSelection.OFF
        _selectedLocations.value = emptyList()

        // Image
        _userImageSelection.value = UserImageSelection.OFF
        _selectedImage.value = null

        // Color
        _userColorSelection.value = UserColorSelection.OFF
        _selectedColorId.value = null

        // Save state
        _saveState.value = SaveTaskState.Idle
        _snackbarMessage.value = null

        refreshRepeatRule()
    }

    private fun validateTask(
        title: String,
        dateSelection: UserDateSelection,
        startDate: LocalDate,
        startTime: LocalTime,
        repeatSelection: UserRepeatSelection,
        weeklyDays: Set<Int>,
        monthlyDaySelection: RepeatMonthlyDaySelection,
        monthlyDays: Set<Int>,
        remindSelection: UserRemindSelection,
        startReminders: List<Reminder>,
        endReminders: List<Reminder>,
        endDate: LocalDate?,
        isEndDateVisible: Boolean,
        endTime: LocalTime?,
        isEndTimeVisible: Boolean
    ): String? {
        // Validation order:
        // 1. Title validation
        // 2. Date/Time validation
        // 3. Repeat validation
        // 4. Reminder validation
        
        // 1. Title validation
        val titleError = AddTaskValidator.validateTitle(title)
        if (titleError != null) return titleError
        
        // 2. Date/Time validation
        val dateTimeError = AddTaskValidator.validateDateTime(
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        )
        if (dateTimeError != null) return dateTimeError
        
        // 3. Repeat validation
        val repeatError = AddTaskValidator.validateRepeat(
            repeatSelection = repeatSelection,
            weeklyDays = weeklyDays,
            monthlyDaySelection = monthlyDaySelection,
            monthlyDays = monthlyDays
        )
        if (repeatError != null) return repeatError
        
        // 4. Reminder validation
        val reminderError = AddTaskValidator.validateReminders(
            remindSelection = remindSelection,
            dateSelection = dateSelection,
            startDate = startDate,
            startTime = startTime,
            startReminders = startReminders,
            endReminders = endReminders,
            endDate = endDate,
            isEndDateVisible = isEndDateVisible,
            endTime = endTime,
            isEndTimeVisible = isEndTimeVisible
        )
        if (reminderError != null) return reminderError
        
        return null // Valid
    }
    
    private fun buildReminders(): List<com.example.bghelp.domain.model.TaskReminderEntry> {
        val start = with(TaskMapper) { 
            _startReminders.value.toDomainReminders(ReminderKind.START)
        }
        val showEndReminders = _isEndTimeVisible.value && _userDateSelection.value != UserDateSelection.ON
        val end = if (showEndReminders) {
            with(TaskMapper) { 
                _endReminders.value.toDomainReminders(ReminderKind.END)
            }
        } else {
            emptyList()
        }
        return start + end
    }

    private suspend fun getSelectedTaskColor(): FeatureColor {
        val colorId = if (_userColorSelection.value == UserColorSelection.OFF) {
            // Default to first color (should be "Default" with id = 1)
            1
        } else {
            _selectedColorId.value ?: 1
        }
        
        // Use cached colors from StateFlow instead of querying database
        val colors = allColors.value
        return colors.find { it.id == colorId }
            ?: FeatureColor(
                id = 1,
                name = "Default",
                red = (TaskDefault.red * 255).toInt(),
                green = (TaskDefault.green * 255).toInt(),
                blue = (TaskDefault.blue * 255).toInt(),
                alpha = TaskDefault.alpha
            )
    }

    private suspend fun persistSelectedImageIfNeeded(): TaskImageAttachment? {
        if (_userImageSelection.value != UserImageSelection.ON) return null
        val selectedImage = _selectedImage.value ?: return null
        return TaskImageStorage.persistTaskImage(appContext, selectedImage)
    }
}
