package com.example.bghelp.domain.constants

import com.example.bghelp.domain.model.CreateFeatureColor
import com.example.bghelp.domain.model.FeatureColor

object ColorSeeds {
    // Colors used to pre-populate the database on first run
    val DefaultColors: List<CreateFeatureColor> = listOf(
        // Match ColorInitializer set
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


