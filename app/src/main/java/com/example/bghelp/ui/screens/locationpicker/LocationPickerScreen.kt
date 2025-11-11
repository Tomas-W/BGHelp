package com.example.bghelp.ui.screens.locationpicker

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.ui.screens.locationpicker.LocationPickerConstants as CONST
import com.example.bghelp.ui.screens.locationpicker.LocationPickerStrings as STR
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.theme.Sizes
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.ArrayList

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LocationPickerScreen(
    navController: NavController,
    allowMultiple: Boolean,
    viewModel: LocationPickerViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val selectedLocations by viewModel.selectedLocations.collectAsState()
    val activeLocationIndex by viewModel.activeLocationIndex.collectAsState()
    val focusedLocationState = viewModel.focusedLocation.collectAsState()
    val focusedLocation = focusedLocationState.value

    val focusManager = LocalFocusManager.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            CONST.DEFAULT_MAP_CENTER,
            CONST.DEFAULT_MAP_ZOOM
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    }
    val mapProperties = remember { MapProperties(isMyLocationEnabled = false) }

    // Allow single or multiple locations to be picked
    LaunchedEffect(allowMultiple) {
        viewModel.configureAllowMultiple(allowMultiple)
    }

    // Load previously selected locations from AddTaskScreen
    LaunchedEffect(navController) {
        val initialLocations = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<ArrayList<TaskLocation>>(LocationNavigationKeys.INITIAL_LOCATIONS)
        if (initialLocations != null) {
            viewModel.loadInitialLocations(initialLocations)
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.remove<ArrayList<TaskLocation>>(LocationNavigationKeys.INITIAL_LOCATIONS)
        }
    }

    // Update the camera position based on the focused location
    LaunchedEffect(focusedLocation) {
        val position = focusedLocation?.position
        if (position != null) {
            val zoom = focusedLocation?.zoom
            val cameraUpdate = if (zoom != null) {
                CameraUpdateFactory.newLatLngZoom(position, zoom)
            } else {
                CameraUpdateFactory.newLatLng(position)
            }
            cameraPositionState.animate(cameraUpdate)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val density = LocalDensity.current
        val safeMaxHeightPx = with(density) { maxHeight.toPx() }
        val minMapHeightPx = with(density) { CONST.MIN_MAP_HEIGHT.toPx() }
        val minDetailsHeightPx = with(density) { CONST.MIN_DETAILS_SECTION_HEIGHT.toPx() }
        val maxMapHeightPx = (safeMaxHeightPx - minDetailsHeightPx).coerceAtLeast(minMapHeightPx)

        var mapHeightPx by remember(safeMaxHeightPx, maxMapHeightPx) {
            mutableFloatStateOf(
                (maxMapHeightPx * CONST.INITIAL_MAP_HEIGHT_RATIO)
                    .coerceIn(minMapHeightPx, maxMapHeightPx)
            )
        }

        // Update the map height based on restrictions
        LaunchedEffect(safeMaxHeightPx, minMapHeightPx, minDetailsHeightPx) {
            mapHeightPx = mapHeightPx.coerceIn(minMapHeightPx, maxMapHeightPx)
        }

        val mapHeight = remember(mapHeightPx, density) { with(density) { mapHeightPx.toDp() } }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mapHeight)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings,
                    properties = mapProperties,
                    onMapClick = { latLng ->
                        focusManager.clearFocus(force = true)
                        viewModel.onMapClick(latLng)
                    }
                ) {
                    selectedLocations.forEachIndexed { index, location ->
                        val markerState = remember(location.latitude, location.longitude) {
                            MarkerState(
                                position = LatLng(location.latitude, location.longitude)
                            )
                        }
                        Marker(
                            state = markerState,
                            title = location.name.ifBlank { location.address },
                            snippet = location.address,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                CONST.MARKER_HUES[index % CONST.MARKER_HUES.size]
                            )
                        )
                    }
                }

                SearchPanel(
                    query = query,
                    suggestions = suggestions,
                    onQueryChange = viewModel::onQueryChange,
                    onSuggestionSelected = { suggestion ->
                        focusManager.clearFocus(force = true)
                        viewModel.onSuggestionSelected(suggestion)
                    }
                )
            }

            DragHandle(
                onDragDelta = { delta ->
                    mapHeightPx = (mapHeightPx + delta)
                        .coerceIn(minMapHeightPx, maxMapHeightPx)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LocationDetails(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    locations = selectedLocations,
                    activeIndex = activeLocationIndex,
                    onFocus = viewModel::onLocationFocused,
                    onNameChange = viewModel::onLocationNameChange,
                    onRemove = viewModel::removeLocation
                )

                BottomButtonRow(
                    viewModel = viewModel,
                    navController = navController,
                    cameraPositionState = cameraPositionState,
                    allowMultiple = allowMultiple,
                    selectedLocations = selectedLocations
                )
            }
        }
    }
}

@Composable
private fun BottomButtonRow(
    viewModel: LocationPickerViewModel,
    navController: NavController,
    cameraPositionState: CameraPositionState,
    allowMultiple: Boolean,
    selectedLocations: List<TaskLocation>
) {
    val canSave = selectedLocations.isNotEmpty() &&
            selectedLocations.all { it.name.isNotBlank() }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val canAddMarker = allowMultiple || selectedLocations.isEmpty()
        OutlinedButton(
            modifier = Modifier.weight(1f),
            enabled = canAddMarker,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            onClick = {
                val target = cameraPositionState.position.target
                viewModel.onAddMarker(target)
            }
        ) {
            Text(text = STR.ADD_MARKER)
        }
        Button(
            modifier = Modifier.weight(1f),
            enabled = canSave,
            onClick = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        LocationNavigationKeys.RESULT,
                        ArrayList(selectedLocations)
                    )
                navController.popBackStack()
            }
        ) {
            Text(
                text = if (selectedLocations.size <= 1) {
                    STR.SAVE_LOCATION
                } else {
                    STR.SAVE_LOCATIONS
                }
            )
        }
    }
}


@Composable
private fun DragHandle(
    onDragDelta: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onDragDelta(dragAmount.y)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(Sizes.Corner.ExtraSmall))
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}
