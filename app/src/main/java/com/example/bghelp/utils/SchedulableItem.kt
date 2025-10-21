package com.example.bghelp.utils


interface SchedulableItem {
    val id: Int
    val date: Long
    val message: String
    val sound: AlarmMode
    val vibrate: AlarmMode
    val snoozeTime: Int
}