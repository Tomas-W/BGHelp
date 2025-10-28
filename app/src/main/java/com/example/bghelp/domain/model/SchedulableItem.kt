package com.example.bghelp.domain.model

import java.time.LocalDateTime

interface SchedulableItem {
    val id: Int
    val date: LocalDateTime
    val title: String
    val description: String?
    val sound: AlarmMode
    val vibrate: AlarmMode
    val snoozeTime: Int
}
