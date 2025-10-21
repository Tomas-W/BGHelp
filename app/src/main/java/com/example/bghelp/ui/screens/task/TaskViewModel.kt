package com.example.bghelp.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.local.TaskEntity
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.utils.getEpochRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }

    val getAllTasks: StateFlow<List<TaskEntity>> =
        taskRepository.getAllTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getTasksInRange(date: Instant? = null): Flow<List<TaskEntity>> {
        if (date == null) {
            val (start, end) = getEpochRange()
            return taskRepository.getTasksByDateRange(start, end)
        } else {
            val (start, end) = getEpochRange(date)
            return taskRepository.getTasksByDateRange(start, end)
        }
    }
}