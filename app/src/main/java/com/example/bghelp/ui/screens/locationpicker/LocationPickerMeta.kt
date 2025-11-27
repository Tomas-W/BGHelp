package com.example.bghelp.ui.screens.locationpicker

import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

object LocationPickerConstants {
    val DEFAULT_MAP_CENTER = LatLng(20.0, 0.0)
    const val DEFAULT_MAP_ZOOM = 2f
    const val INITIAL_MAP_HEIGHT_RATIO = 0.9f
    const val SEARCH_RESULT_ZOOM = 12f
    val MIN_MAP_HEIGHT = 240.dp
    val MIN_DETAILS_SECTION_HEIGHT = 180.dp
    val MARKER_HUES = listOf(
        BitmapDescriptorFactory.HUE_RED,
        BitmapDescriptorFactory.HUE_ORANGE,
        BitmapDescriptorFactory.HUE_GREEN,
        BitmapDescriptorFactory.HUE_CYAN,
        BitmapDescriptorFactory.HUE_MAGENTA,
        BitmapDescriptorFactory.HUE_ROSE,
        BitmapDescriptorFactory.HUE_YELLOW,
        BitmapDescriptorFactory.HUE_VIOLET,
        BitmapDescriptorFactory.HUE_AZURE,
        BitmapDescriptorFactory.HUE_BLUE,
    )
}

object LocationNavigationKeys {
    const val RESULT = "location_picker_result"
    const val INITIAL_LOCATIONS = "location_picker_initial_locations"
}

