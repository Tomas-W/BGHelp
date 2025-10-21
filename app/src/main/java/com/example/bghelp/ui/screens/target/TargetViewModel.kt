package com.example.bghelp.ui.screens.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.data.local.TargetEntity
import com.example.bghelp.data.repository.TargetRepository
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
class TargetViewModel @Inject constructor(
    private val targetRepository: TargetRepository
): ViewModel() {

    fun addTarget(target: TargetEntity) {
        viewModelScope.launch {
            targetRepository.addTarget(target)
        }
    }

    val getAllTargets: StateFlow<List<TargetEntity>> =
        targetRepository.getAllTargets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getTargetsInRange(date: Instant? = null): Flow<List<TargetEntity>> {
        if (date == null) {
            val (start, end) = getEpochRange()
            return targetRepository.getTargetByDateRange(start, end)
        } else {
            val (start, end) = getEpochRange(date)
            return targetRepository.getTargetByDateRange(start, end)
        }
    }


}