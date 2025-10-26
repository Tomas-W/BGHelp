package com.example.bghelp.domain.model

enum class AlarmMode(val value: String) {
    OFF("off"),
    ONCE("once"),
    CONTINUOUS("continuous");

    companion object {
        private val map = AlarmMode.entries.associateBy(AlarmMode::value)
        fun fromValue(value: String) = map[value]
            ?: throw IllegalArgumentException("Unknown AlarmMode: $value")
    }
}
