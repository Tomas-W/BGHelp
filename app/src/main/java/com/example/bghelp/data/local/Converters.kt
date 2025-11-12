package com.example.bghelp.data.local

import androidx.room.TypeConverter
import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.Coordinate
import com.example.bghelp.domain.model.ReminderKind
import com.example.bghelp.domain.model.ReminderOffsetUnit
import com.example.bghelp.domain.model.TaskColorOption
import com.example.bghelp.domain.model.TaskImageSourceOption
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // Alarm mode
    @TypeConverter
    fun fromAlarmMode(mode: AlarmMode): String = mode.value

    @TypeConverter
    fun toAlarmMode(value: String): AlarmMode = AlarmMode.fromValue(value)

    // Coordinates
    @TypeConverter
    fun fromCoordinatesList(coords: List<Coordinate>): String {
        return gson.toJson(coords)
    }

    @TypeConverter
    fun toCoordinatesList(json: String): List<Coordinate> {
        val type = object : TypeToken<List<Coordinate>>() {}.type
        return gson.fromJson(json, type)
    }

    // Task color
    @TypeConverter
    fun fromTaskColor(color: TaskColorOption): String = color.name

    @TypeConverter
    fun toTaskColor(value: String): TaskColorOption = runCatching {
        TaskColorOption.valueOf(value)
    }.getOrElse { TaskColorOption.DEFAULT }

    // Reminder type
    @TypeConverter
    fun fromReminderType(type: ReminderKind): String = type.name

    @TypeConverter
    fun toReminderType(value: String): ReminderKind = runCatching {
        ReminderKind.valueOf(value)
    }.getOrElse { ReminderKind.START }

    // Reminder offset unit
    @TypeConverter
    fun fromReminderOffsetUnit(unit: ReminderOffsetUnit): String = unit.name

    @TypeConverter
    fun toReminderOffsetUnit(value: String): ReminderOffsetUnit = runCatching {
        ReminderOffsetUnit.valueOf(value)
    }.getOrElse { ReminderOffsetUnit.MINUTES }

    // Task image source
    @TypeConverter
    fun fromTaskImageSource(source: TaskImageSourceOption?): String? = source?.name

    @TypeConverter
    fun toTaskImageSource(value: String?): TaskImageSourceOption? = value?.let {
        runCatching { TaskImageSourceOption.valueOf(it) }.getOrNull()
    }
}
