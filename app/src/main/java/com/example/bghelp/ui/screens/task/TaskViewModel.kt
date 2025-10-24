package com.example.bghelp.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.Task
import com.example.bghelp.utils.getEpochRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    fun addTask(createTask: CreateTask) {
        viewModelScope.launch {
            taskRepository.addTask(createTask)
            // Room automatically updates tasksInRange via flatMapLatest!
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    // Store the current date range
    private val _selectedDate = MutableStateFlow<Instant?>(null)
    val selectedDate: StateFlow<Instant?> = _selectedDate.asStateFlow()

    // Efficient database-level filtering with Room's built-in reactivity
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksInRange: StateFlow<List<Task>> =
        _selectedDate
            .flatMapLatest { date ->
                val (start, end) = if (date == null) {
                    getEpochRange() // Default range
                } else {
                    getEpochRange(date)
                }
                taskRepository.getTasksByDateRange(start, end) // ← Database-level filtering
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun updateDateRange(date: Instant?) {
        _selectedDate.value = date
    }

    // Keep for testing if needed
    val getAllTasks: StateFlow<List<Task>> =
        taskRepository.getAllTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}