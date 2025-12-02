package com.example.bghelp.domain.constants

import com.example.bghelp.domain.model.AlarmMode
import com.example.bghelp.domain.model.CreateFeatureColor
import com.example.bghelp.domain.model.CreateTask
import com.example.bghelp.domain.model.FeatureColor
import com.example.bghelp.domain.model.ReminderOffsetUnit
import java.time.LocalDateTime

object ColorSeeds {
    // Colors used to pre-populate the database on first run
    val DefaultColors: List<CreateFeatureColor> = listOf(
        CreateFeatureColor(
            name = "Default",
            isDefault = true,
            lightRed = 200, lightGreen = 220, lightBlue = 245, lightAlpha = 1.0f,
            darkRed = 200, darkGreen = 220, darkBlue = 245, darkAlpha = 1.0f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        ),
        CreateFeatureColor(
            name = "Red",
            isDefault = true,
            lightRed = 255, lightGreen = 0, lightBlue = 0, lightAlpha = 0.12f,
            darkRed = 255, darkGreen = 0, darkBlue = 0, darkAlpha = 0.12f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        ),
        CreateFeatureColor(
            name = "Green",
            isDefault = true,
            lightRed = 0, lightGreen = 255, lightBlue = 0, lightAlpha = 0.12f,
            darkRed = 0, darkGreen = 255, darkBlue = 0, darkAlpha = 0.12f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        ),
        CreateFeatureColor(
            name = "Yellow",
            isDefault = true,
            lightRed = 255, lightGreen = 255, lightBlue = 0, lightAlpha = 0.12f,
            darkRed = 255, darkGreen = 255, darkBlue = 0, darkAlpha = 0.12f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        ),
        CreateFeatureColor(
            name = "Cyan",
            isDefault = true,
            lightRed = 0, lightGreen = 255, lightBlue = 255, lightAlpha = 0.12f,
            darkRed = 0, darkGreen = 255, darkBlue = 255, darkAlpha = 0.12f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        ),
        CreateFeatureColor(
            name = "Magenta",
            isDefault = true,
            lightRed = 255, lightGreen = 0, lightBlue = 255, lightAlpha = 0.12f,
            darkRed = 255, darkGreen = 0, darkBlue = 255, darkAlpha = 0.12f,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        )
    )

    val FallbackTaskColor: FeatureColor = DefaultColors.first().let {
        FeatureColor(
            id = 0,
            name = it.name,
            lightRed = it.lightRed, lightGreen = it.lightGreen, lightBlue = it.lightBlue, lightAlpha = it.lightAlpha,
            darkRed = it.darkRed, darkGreen = it.darkGreen, darkBlue = it.darkBlue, darkAlpha = it.darkAlpha,
            lightTextRed = 0, lightTextGreen = 0, lightTextBlue = 0, lightTextAlpha = 1.0f,
            darkTextRed = 0, darkTextGreen = 0, darkTextBlue = 0, darkTextAlpha = 1.0f
        )
    }
}

object TaskSeeds {
    private val loremWords = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
        "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud",
        "exercitation", "ullamco", "laboris", "nisi", "aliquip", "ex", "ea", "commodo",
        "consequat", "duis", "aute", "irure", "in", "reprehenderit", "voluptate", "velit",
        "esse", "cillum", "fugiat", "nulla", "pariatur", "excepteur", "sint", "occaecat",
        "cupidatat", "non", "proident", "sunt", "culpa", "qui", "officia", "deserunt",
        "mollit", "anim", "id", "est", "laborum"
    )

    private val alarmNames = listOf("Classic", "Rooster", "Morning", "Chime", "Bell", "Alert", "Beep", "Tone")

    private val noteOptions = listOf("Note A", "Note B", "Note C", "Note D", "Note E")

    private fun randomLoremWords(count: Int): String {
        return (1..count).joinToString(" ") { loremWords.random() }
    }

    private fun randomLoremWordsOnePerLine(count: Int): String {
        return (1..count).joinToString("\n") { loremWords.random() }
    }

    private fun randomLoremWordsWithNewlines(wordCount: Int, newlineCount: Int): String {
        val words = (1..wordCount).map { loremWords.random() }
        val newlinePositions = (1 until wordCount).shuffled().take(newlineCount).sorted()
        return words.mapIndexed { index, word ->
            if (index in newlinePositions) "\n$word" else word
        }.joinToString(" ")
    }

    private fun randomAlarmName(): String? = alarmNames.random()

    private fun randomAlarmMode(): AlarmMode = AlarmMode.entries.random()
    private fun randomSnoozeSeconds(): Int = (1000..106400).random()
    private fun randomSnoozeValue1(): Int = listOf(5, 10, 15, 30).random()
    private fun randomSnoozeUnit1(): ReminderOffsetUnit = ReminderOffsetUnit.MINUTES
    private fun randomSnoozeValue2(): Int = listOf(1, 2, 3).random()
    private fun randomSnoozeUnit2(): ReminderOffsetUnit = ReminderOffsetUnit.HOURS

    private fun randomNote(): String? = noteOptions.random()

    fun generateDefaultTasks(availableColors: List<FeatureColor>, now: LocalDateTime): List<CreateTask> {
        val defaultColor = availableColors.firstOrNull() ?: ColorSeeds.FallbackTaskColor
        val randomColor = availableColors.randomOrNull() ?: defaultColor

        val task1Date = now.plusHours(1)
        val task2Date = now.plusDays(1)
        val task3Date = now
        val task4Date = now.plusDays(1)

        return listOf(
            // Task 1: date in an hour, 4 words title, 75 lines description, endDate 2 hours after date
            CreateTask(
                startDate = task1Date,
                title = randomLoremWords(4),
                info = randomLoremWordsOnePerLine(75),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeSeconds = randomSnoozeSeconds(),
                snoozeValue1 = randomSnoozeValue1(),
                snoozeUnit1 = randomSnoozeUnit1(),
                snoozeValue2 = randomSnoozeValue2(),
                snoozeUnit2 = randomSnoozeUnit2(),
                endDate = task1Date.plusHours(2),
                allDay = false,
                note = randomNote(),
                color = defaultColor
            ),
            // Task 2: date tomorrow, 3 words title, 75 lines description, endDate 3 hours after date
            CreateTask(
                startDate = task2Date,
                title = randomLoremWords(3),
                info = randomLoremWordsOnePerLine(75),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeSeconds = randomSnoozeSeconds(),
                snoozeValue1 = randomSnoozeValue1(),
                snoozeUnit1 = randomSnoozeUnit1(),
                snoozeValue2 = randomSnoozeValue2(),
                snoozeUnit2 = randomSnoozeUnit2(),
                endDate = task2Date.plusHours(3),
                allDay = false,
                note = randomNote(),
                color = defaultColor
            ),
            // Task 3: date now, 3 words title, 15 words with 2 newlines, no endDate, random color
            CreateTask(
                startDate = task3Date,
                title = randomLoremWords(3),
                info = randomLoremWordsWithNewlines(15, 2),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeSeconds = randomSnoozeSeconds(),
                snoozeValue1 = randomSnoozeValue1(),
                snoozeUnit1 = randomSnoozeUnit1(),
                snoozeValue2 = randomSnoozeValue2(),
                snoozeUnit2 = randomSnoozeUnit2(),
                endDate = null,
                allDay = false,
                note = randomNote(),
                color = randomColor
            ),
            // Task 4: date tomorrow, 3 words title, 15 words with 2 newlines, allDay=true, no endDate, random color
            CreateTask(
                startDate = task4Date,
                title = randomLoremWords(3),
                info = randomLoremWordsWithNewlines(15, 2),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeSeconds = randomSnoozeSeconds(),
                snoozeValue1 = randomSnoozeValue1(),
                snoozeUnit1 = randomSnoozeUnit1(),
                snoozeValue2 = randomSnoozeValue2(),
                snoozeUnit2 = randomSnoozeUnit2(),
                endDate = null,
                allDay = true,
                note = randomNote(),
                color = randomColor
            )
        )
    }
}


