package com.example.bghelp.utils

import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.TimeUnit

object TimeUnitMapper {
    fun TimeUnit.toReminderOffsetUnit(): ReminderOffsetUnit = when (this) {
        TimeUnit.MINUTES -> ReminderOffsetUnit.MINUTES
        TimeUnit.HOURS -> ReminderOffsetUnit.HOURS
        TimeUnit.DAYS -> ReminderOffsetUnit.DAYS
        TimeUnit.WEEKS -> ReminderOffsetUnit.WEEKS
        TimeUnit.MONTHS -> ReminderOffsetUnit.MONTHS
    }

    fun ReminderOffsetUnit.toTimeUnit(): TimeUnit = when (this) {
        ReminderOffsetUnit.MINUTES -> TimeUnit.MINUTES
        ReminderOffsetUnit.HOURS -> TimeUnit.HOURS
        ReminderOffsetUnit.DAYS -> TimeUnit.DAYS
        ReminderOffsetUnit.WEEKS -> TimeUnit.WEEKS
        ReminderOffsetUnit.MONTHS -> TimeUnit.MONTHS
    }
}

