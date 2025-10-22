package com.example.bghelp.data.local

import androidx.room.TypeConverter
import com.example.bghelp.utils.AlarmMode
import com.example.bghelp.utils.Coordinate
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
}
