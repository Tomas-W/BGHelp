package com.example.bghelp.ui.screens.locationpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.screens.locationpicker.LocationPickerStrings as STR
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.TextBlack
import com.example.bghelp.ui.theme.TextStyles

@Composable
fun SearchPanel(
    query: String,
    suggestions: List<LocationSuggestion>,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (LocationSuggestion) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = STR.SEARCH_PLACEHOLDER,
                    style = TextStyles.Default.M
                )
            },
            textStyle = TextStyles.Default.M,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = STR.SEARCH_ICON_DESCRIPTION
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = STR.CLEAR_SEARCH_DESCRIPTION
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
                    .clip(RoundedCornerShape(Sizes.Corner.XS))
                    .background(MaterialTheme.colorScheme.surface)
                    .shadow(6.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
            style = TextStyles.Default.S,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (suggestion.subtitle.isNotEmpty()) {
            Text(
                text = suggestion.subtitle,
                style = TextStyles.Default.Italic.XS,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}