package com.example.bghelp.ui.screens.locationpicker

import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

data class LocationSuggestion(
    val placeId: String,
    val title: String,
    val subtitle: String
)

data class FocusedLocation(
    val position: LatLng,
    val zoom: Float? = null
)

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    @ApplicationContext context: android.content.Context
) : ViewModel() {

    private val geocoder = Geocoder(context, Locale.getDefault())
    private val ioDispatcher = Dispatchers.IO

    private var allowMultiple: Boolean = false

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val suggestions: StateFlow<List<LocationSuggestion>> = _suggestions.asStateFlow()

    private val _selectedLocations = MutableStateFlow<List<TaskLocation>>(emptyList())
    val selectedLocations: StateFlow<List<TaskLocation>> = _selectedLocations.asStateFlow()

    private val _activeLocationIndex = MutableStateFlow<Int?>(null)
    val activeLocationIndex: StateFlow<Int?> = _activeLocationIndex.asStateFlow()

    private val _focusedLocation = MutableStateFlow<FocusedLocation?>(null)
    val focusedLocation: StateFlow<FocusedLocation?> = _focusedLocation.asStateFlow()

    private var searchJob: Job? = null
    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    fun configureAllowMultiple(isAllowed: Boolean) {
        allowMultiple = isAllowed
    }

    // Load previously selected locations from AddTaskScreen
    fun loadInitialLocations(locations: List<TaskLocation>) {
        _selectedLocations.value = locations.map { it.copy() }
        if (locations.isEmpty()) {
            setActiveLocation(null)
        } else {
            setActiveLocation(locations.lastIndex)
        }
    }

    // Handle query changes from the SearchPanel
    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()
        if (value.length < 2) {
            _suggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(250)
            loadSuggestions(value)
        }
    }

    // Handle suggestion selection from the SearchPanel
    fun onSuggestionSelected(suggestion: LocationSuggestion) {
        viewModelScope.launch {
            runCatching {
                val request = FetchPlaceRequest.newInstance(
                    suggestion.placeId,
                    listOf(
                        Field.ID,
                        Field.NAME,
                        Field.ADDRESS,
                        Field.LAT_LNG
                    )
                )
                placesClient.fetchPlace(request).await().place
            }.onSuccess { place ->
                sessionToken = AutocompleteSessionToken.newInstance()
                _suggestions.value = emptyList()
                place.latLng?.let { latLng ->
                    val address = place.address ?: place.name ?: formatCoordinatePair(latLng)
                    applySearchSuggestionLocation(
                        latLng = latLng,
                        address = address
                    )
                    _query.value = place.name ?: address
                }
            }.onFailure { throwable ->
                Log.e("LocationPicker", "Failed to fetch place", throwable)
            }
        }
    }

    fun onMapClick(latLng: LatLng) {
        dismissSuggestions()
        val index = _activeLocationIndex.value ?: return
        viewModelScope.launch {
            val address = resolveAddress(latLng) ?: formatCoordinatePair(latLng)
            updateLocationAt(
                index = index,
                transform = { current ->
                    current.copy(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        address = address
                    )
                }
            )
            _suggestions.value = emptyList()
            _query.value = address
        }
    }

    fun dismissSuggestions() {
        searchJob?.cancel()
        _suggestions.value = emptyList()
    }

    fun onAddMarker(latLng: LatLng) {
        viewModelScope.launch {
            val address = resolveAddress(latLng) ?: formatCoordinatePair(latLng)
            _selectedLocations.update { current ->
                when {
                    current.isEmpty() -> {
                        listOf(
                            TaskLocation(
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                address = address
                            )
                        )
                    }

                    allowMultiple -> {
                        current + TaskLocation(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            address = address
                        )
                    }

                    else -> {
                        listOf(
                            TaskLocation(
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                address = address
                            ).copy(
                                name = current.first().name
                            )
                        )
                    }
                }
            }
            val newMarkerIndex = _selectedLocations.value.lastIndex
            setActiveLocation(newMarkerIndex)
            _suggestions.value = emptyList()
            _query.value = address
        }
    }

    fun removeLocation(index: Int) {
        val currentActive = _activeLocationIndex.value
        _selectedLocations.update { current ->
            if (index !in current.indices) {
                current
            } else {
                current.filterIndexed { itemIndex, _ -> itemIndex != index }
            }
        }
        val newSize = _selectedLocations.value.size
        val newIndex = when {
            newSize == 0 -> null
            currentActive == null -> null
            index < currentActive -> currentActive - 1
            index == currentActive -> minOf(index, newSize - 1)
            else -> currentActive
        }
        setActiveLocation(newIndex)
    }

    fun onLocationNameChange(index: Int, value: String) {
        updateLocationAt(
            index = index,
            transform = { current ->
                current.copy(name = value)
            }
        )
    }

    fun onLocationFocused(index: Int) {
        if (index !in _selectedLocations.value.indices) return
        setActiveLocation(index)
        val location = _selectedLocations.value[index]
        _query.value = location.name.ifBlank { location.address }
        dismissSuggestions()
    }

    private suspend fun loadSuggestions(value: String) {
        runCatching {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(value)
                .setSessionToken(sessionToken)
                .build()
            placesClient.findAutocompletePredictions(request).await()
        }.onSuccess { response ->
            _suggestions.value = response.autocompletePredictions.map { prediction ->
                LocationSuggestion(
                    placeId = prediction.placeId,
                    title = prediction.getPrimaryText(null).toString(),
                    subtitle = prediction.getSecondaryText(null).toString()
                )
            }
        }.onFailure { throwable ->
            Log.e("LocationPicker", "Failed to load suggestions", throwable)
            _suggestions.value = emptyList()
        }
    }

    private suspend fun resolveAddress(latLng: LatLng): String? = withContext(ioDispatcher) {
        if (!Geocoder.isPresent()) {
            return@withContext null
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<android.location.Address>) {
                                if (continuation.isCompleted) return
                                continuation.resume(
                                    addresses.firstOrNull()?.getAddressLine(0)
                                )
                            }

                            override fun onError(errorMessage: String?) {
                                if (continuation.isCompleted) return
                                continuation.resume(null)
                            }
                        }
                    )
                    continuation.invokeOnCancellation { }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    ?.firstOrNull()
                    ?.getAddressLine(0)
            }
        } catch (throwable: IOException) {
            null
        } catch (throwable: Exception) {
            Log.e("LocationPicker", "Geocoding failed", throwable)
            null
        }
    }

    private fun formatCoordinatePair(latLng: LatLng): String =
        "${formatCoordinate(latLng.latitude)}, ${formatCoordinate(latLng.longitude)}"

    private fun formatCoordinate(value: Double): String =
        String.format(Locale.getDefault(), "%.5f", value)

    private fun applySearchSuggestionLocation(
        latLng: LatLng,
        address: String
    ) {
        val targetIndex = _activeLocationIndex.value
        val nextLocation = TaskLocation(
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            address = address
        )
        _selectedLocations.update { current ->
            when {
                targetIndex != null && targetIndex in current.indices -> {
                    current.mapIndexed { index, item ->
                        if (index == targetIndex) {
                            nextLocation.copy(name = item.name)
                        } else {
                            item
                        }
                    }
                }

                allowMultiple -> current + nextLocation
                else -> listOf(nextLocation)
            }
        }
        val activeIndex = when {
            targetIndex != null && targetIndex in _selectedLocations.value.indices -> targetIndex
            _selectedLocations.value.isEmpty() -> null
            else -> _selectedLocations.value.lastIndex
        }
        setActiveLocation(activeIndex)
        _focusedLocation.value = FocusedLocation(
            position = latLng,
            zoom = LocationPickerConstants.SEARCH_RESULT_ZOOM
        )
    }

    private fun updateLocationAt(
        index: Int,
        transform: (TaskLocation) -> TaskLocation
    ) {
        _selectedLocations.update { current ->
            if (index !in current.indices) {
                current
            } else {
                current.mapIndexed { itemIndex, item ->
                    if (itemIndex == index) transform(item) else item
                }
            }
        }
    }

    private fun setActiveLocation(index: Int?) {
        _activeLocationIndex.value = index
    }
}

