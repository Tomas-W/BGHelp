package com.example.bghelp.ui.screens.locationpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.screens.locationpicker.LocationPickerStrings as STR
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextStyles


@Composable
fun LocationDetails(
    modifier: Modifier = Modifier,
    locations: List<TaskLocation>,
    activeIndex: Int?,
    onFocus: (Int) -> Unit,
    onNameChange: (Int, String) -> Unit,
    onRemove: (Int) -> Unit
) {
    if (locations.isEmpty()) {
        Text(
            modifier = modifier
                .fillMaxWidth(),
            text = STR.EMPTY_LOCATIONS_HINT,
            style = TextStyles.Default.Small,
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
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
                contentDescription = STR.DELETE_LOCATION_DESCRIPTION
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
                    text = STR.NAME_PLACEHOLDER,
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
