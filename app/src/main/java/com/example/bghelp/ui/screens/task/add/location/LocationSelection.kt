package com.example.bghelp.ui.screens.task.add.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.bghelp.R
import com.example.bghelp.ui.screens.task.add.TaskLocation
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.selectedStyle
import com.example.bghelp.ui.theme.Sizes
import kotlin.collections.forEach

@Composable
fun LocationSelection(
    locations: List<TaskLocation>,
    onAddLocation: () -> Unit,
    onRemoveLocation: (TaskLocation) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.S)
    ) {
        Text(
            modifier = Modifier
                .clickable { onAddLocation() },
            text = "Add location",
            style = deselectedStyle
        )

        locations.forEach { location ->
            HorizontalDivider()
            LocationSummary(
                location = location,
                onRemove = { onRemoveLocation(location) }
            )
        }
    }
}

@Composable
private fun LocationSummary(
    location: TaskLocation,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = location.name.ifBlank { "unnamed" },
                style = selectedStyle
            )

            Text(
                text = location.address,
                style = deselectedStyle
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(
                modifier = Modifier.size(Sizes.Icon.L),
                onClick = onRemove
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Delete address"
                )
            }
        }
    }
}