package com.example.bghelp.ui.screens.locationpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextBlack
import com.example.bghelp.ui.theme.TextStyles
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
    val focusedLocation by viewModel.focusedLocation.collectAsState()

    val focusManager = LocalFocusManager.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_MAP_CENTER, DEFAULT_MAP_ZOOM)
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false
        )
    }
    val mapProperties = remember { MapProperties(isMyLocationEnabled = false) }

    LaunchedEffect(allowMultiple) {
        viewModel.configureAllowMultiple(allowMultiple)
    }

    LaunchedEffect(focusedLocation) {
        focusedLocation?.let { latLng ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                            MARKER_HUES[index % MARKER_HUES.size]
                        )
                    )
                }
            }

            SearchPanel(
                query = query,
                suggestions = suggestions,
                onQueryChange = viewModel::onQueryChange,
                onSuggestionSelected = viewModel::onSuggestionSelected
            )
        }

        LocationDetailsSection(
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

@Composable
fun BottomButtonRow(
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
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            Text(text = "Add marker")
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
            Text(text = if (selectedLocations.size <= 1) "Save location" else "Save locations")
        }
    }
}

@Composable
private fun SearchPanel(
    query: String,
    suggestions: List<LocationSuggestion>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (LocationSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Search address or place",
                    style = TextStyles.Default.Small
                )
            },
            textStyle = TextStyles.Default.Small,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = TextBlack,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                focusedLeadingIconColor = TextBlack,
                unfocusedLeadingIconColor = TextBlack
            )
        )

        if (suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Sizes.Corner.ExtraSmall))
                    .shadow(6.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionRow(
                            suggestion = suggestion,
                            onSelect = onSuggestionSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionRow(
    suggestion: LocationSuggestion,
    onSelect: (LocationSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(suggestion) }
    ) {
        Text(
            text = suggestion.title,
            style = TextStyles.Default.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (suggestion.subtitle.isNotEmpty()) {
            Text(
                text = suggestion.subtitle,
                style = TextStyles.Default.Italic.ExtraSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LocationDetailsSection(
    locations: List<TaskLocation>,
    activeIndex: Int?,
    onFocus: (Int) -> Unit,
    onNameChange: (Int, String) -> Unit,
    onRemove: (Int) -> Unit
) {
    if (locations.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Sizes.Icon.Small),
            text = "Tap Add marker to create a location, then tap the map or choose a suggestion to position it.",
            style = TextStyles.Default.Small,
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(
                items = locations,
                key = { index, location ->
                    "${index}_${location.latitude}_${location.longitude}"
                }
            ) { index, location ->
                LocationDetailsItem(
                    location = location,
                    isActive = index == activeIndex,
                    onFocus = { onFocus(index) },
                    onNameChange = { value -> onNameChange(index, value) },
                    onRemove = { onRemove(index) }
                )
            }
        }
    }
}

@Composable
private fun LocationDetailsItem(
    location: TaskLocation,
    isActive: Boolean,
    onFocus: () -> Unit,
    onNameChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Sizes.Corner.ExtraSmall))
            .background(
                if (isActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable {
                focusManager.clearFocus(force = true)
                onFocus()
            },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LocationNameField(
                modifier = Modifier.weight(1f),
                value = location.name,
                onValueChange = onNameChange,
                isActive = isActive,
                onFocused = onFocus
            )
            Icon(
                modifier = Modifier
                    .size(Sizes.Icon.Small)
                    .clickable {
                        focusManager.clearFocus(force = true)
                        onRemove()
                    },
                imageVector = Icons.Default.Close,
                contentDescription = "Delete location"
            )
        }
        Text(
            text = location.address,
            style = TextStyles.Default.Small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LocationNameField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isActive: Boolean,
    onFocused: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val underlineColor = if (isFocused || isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Column(modifier = modifier) {
        Box {
            if (value.isEmpty()) {
                Text(
                    text = "Name",
                    style = TextStyles.Default.Italic.Small
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyles.Default.Small.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        val currentlyFocused = focusState.isFocused
                        if (currentlyFocused) {
                            onFocused()
                        }
                        isFocused = currentlyFocused
                    }
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = underlineColor)
        )
    }
}

private val DEFAULT_MAP_CENTER = LatLng(20.0, 0.0)
private const val DEFAULT_MAP_ZOOM = 2f
private val MARKER_HUES = listOf(
    BitmapDescriptorFactory.HUE_RED,
    BitmapDescriptorFactory.HUE_ORANGE,
    BitmapDescriptorFactory.HUE_GREEN,
    BitmapDescriptorFactory.HUE_CYAN,
    BitmapDescriptorFactory.HUE_MAGENTA,
    BitmapDescriptorFactory.HUE_ROSE,
    BitmapDescriptorFactory.HUE_YELLOW,
    BitmapDescriptorFactory.HUE_VIOLET
)

