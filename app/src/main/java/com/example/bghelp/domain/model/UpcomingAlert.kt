package com.example.bghelp.domain.model

import java.time.LocalDateTime
import java.time.ZoneId

data class UpcomingAlert(
    val taskId: Int,
    val triggerTime: LocalDateTime,
    val reminder: TaskReminderEntry?
) {
    fun getUniqueKey(): String {
        val epochMilli = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return "${taskId}_${epochMilli}"
    }
}

