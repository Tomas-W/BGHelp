package com.example.bghelp.ui.screens.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.repository.TaskRepository
import com.example.bghelp.domain.model.Task
import com.example.bghelp.domain.model.UpcomingAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AlertTrackingViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _triggeredAlert = MutableStateFlow<UpcomingAlert?>(null)
    val triggeredAlert: StateFlow<UpcomingAlert?> = _triggeredAlert.asStateFlow()

    private val _triggeredTask = MutableStateFlow<Task?>(null)
    val triggeredTask: StateFlow<Task?> = _triggeredTask.asStateFlow()

    private val triggeredAlertKeys = mutableSetOf<String>()

    private var upcomingAlerts: List<UpcomingAlert> = emptyList()

    init {
        // Start observing upcoming alerts
        viewModelScope.launch {
            taskRepository.getUpcomingAlerts(limit = 5).collect { alerts ->
                upcomingAlerts = alerts
            }
        }

        // Start polling for alerts
        viewModelScope.launch {
            while (true) {
                checkAndTriggerAlerts()
                delay(1000)
            }
        }
    }

    private suspend fun checkAndTriggerAlerts() {
        val now = LocalDateTime.now(ZoneId.systemDefault())

        for (alert in upcomingAlerts) {
            val key = alert.getUniqueKey()

            // Skip if already triggered
            if (key in triggeredAlertKeys) continue

            // Check if trigger time has passed
            if (alert.triggerTime.isBefore(now) || alert.triggerTime.isEqual(now)) {
                // Mark as triggered
                triggeredAlertKeys.add(key)

                // Load full task object
                val task = taskRepository.getTaskById(alert.taskId).first()

                if (task != null) {
                    _triggeredAlert.value = alert
                    _triggeredTask.value = task
                    break // Only trigger one alert at a time
                }
            }
        }
    }

    fun dismissAlert() {
        _triggeredAlert.value = null
        _triggeredTask.value = null
    }
}

