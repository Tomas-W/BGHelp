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
        // Match DatabaseInitializer set
        CreateFeatureColor(name = "Default", red = 200, green = 220, blue = 245, alpha = 1.0f, isDefault = true),
        CreateFeatureColor(name = "Red", red = 255, green = 0, blue = 0, alpha = 0.12f, isDefault = true),
        CreateFeatureColor(name = "Green", red = 0, green = 255, blue = 0, alpha = 0.12f, isDefault = true),
        CreateFeatureColor(name = "Yellow", red = 255, green = 255, blue = 0, alpha = 0.12f, isDefault = true),
        CreateFeatureColor(name = "Cyan", red = 0, green = 255, blue = 255, alpha = 0.12f, isDefault = true),
        CreateFeatureColor(name = "Magenta", red = 255, green = 0, blue = 255, alpha = 0.12f, isDefault = true)
    )

    val FallbackTaskColor: FeatureColor = DefaultColors.first().let {
        FeatureColor(
            id = 0,
            name = it.name,
            red = it.red,
            green = it.green,
            blue = it.blue,
            alpha = it.alpha
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
        return (1..count).map { loremWords.random() }.joinToString(" ")
    }

    private fun randomLoremWordsOnePerLine(count: Int): String {
        return (1..count).map { loremWords.random() }.joinToString("\n")
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
    private fun randomSnoozeTime(): Int = listOf(5, 10, 15, 30).random()
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
                date = task1Date,
                title = randomLoremWords(4),
                description = randomLoremWordsOnePerLine(75),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeTime = randomSnoozeTime(),
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
                date = task2Date,
                title = randomLoremWords(3),
                description = randomLoremWordsOnePerLine(75),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeTime = randomSnoozeTime(),
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
                date = task3Date,
                title = randomLoremWords(3),
                description = randomLoremWordsWithNewlines(15, 2),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeTime = randomSnoozeTime(),
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
                date = task4Date,
                title = randomLoremWords(3),
                description = randomLoremWordsWithNewlines(15, 2),
                expired = false,
                alarmName = randomAlarmName(),
                sound = randomAlarmMode(),
                vibrate = randomAlarmMode(),
                snoozeTime = randomSnoozeTime(),
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


