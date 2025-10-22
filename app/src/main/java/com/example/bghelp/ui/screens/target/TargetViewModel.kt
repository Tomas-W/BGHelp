package com.example.bghelp.ui.screens.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.domain.model.Target
import com.example.bghelp.domain.model.CreateTarget
import com.example.bghelp.data.repository.TargetRepository
import com.example.bghelp.utils.getEpochRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
class TargetViewModel @Inject constructor(
    private val targetRepository: TargetRepository
): ViewModel() {

    fun addTarget(createTarget: CreateTarget) {
        viewModelScope.launch {
            targetRepository.addTarget(createTarget)
            // Room automatically updates targetsInRange via flatMapLatest!
        }
    }

    fun deleteTarget(target: Target) {
        viewModelScope.launch {
            targetRepository.deleteTarget(target)
        }
    }

    // Store the current date range
    private val _selectedDate = MutableStateFlow<Instant?>(null)
    val selectedDate: StateFlow<Instant?> = _selectedDate.asStateFlow()

    // Efficient database-level filtering with Room's built-in reactivity
    @OptIn(ExperimentalCoroutinesApi::class)
    val targetsInRange: StateFlow<List<Target>> =
        _selectedDate
            .flatMapLatest { date ->
                val (start, end) = if (date == null) {
                    getEpochRange()  // Default range
                } else {
                    getEpochRange(date)
                }
                targetRepository.getTargetByDateRange(start, end)  // ← Database-level filtering
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
    val getAllTargets: StateFlow<List<Target>> =
        targetRepository.getAllTargets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}