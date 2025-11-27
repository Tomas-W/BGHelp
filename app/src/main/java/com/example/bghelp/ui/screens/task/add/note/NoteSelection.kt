package com.example.bghelp.ui.screens.task.add.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.bghelp.R
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.deselectedDropdownStyle
import com.example.bghelp.ui.components.selectedDropdownStyle
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextDefault

@Composable
fun NoteSelection(
    selectedNote: String,
    onNoteSelected: (String) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    onDropdownClick: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    val noteOptions = remember { listOf("item1", "item2", "item3") }
    val defaultNoteName = stringResource(R.string.note_select_note)
    val displayText = remember(selectedNote) {
        selectedNote.ifEmpty { defaultNoteName }
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.lTextDefault,
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onDropdownClick()
                    isExpanded = true
                }
        )

        CustomDropdown(
            modifier = Modifier.wrapContentWidth(),
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownItem(
                label = stringResource(R.string.note_select_note),
                onClick = {
                    onNoteSelected("")
                    isExpanded = false
                },
                textStyle = if (selectedNote == "") {
                    selectedDropdownStyle()
                } else {
                    deselectedDropdownStyle()
                },
                spacing = Sizes.Icon.M
            )
            DropdownItem(
                label = stringResource(R.string.note_create_note),
                onClick = {
                    onNoteSelected("CREATE")
                    isExpanded = false
                },
                textStyle = if (selectedNote == "CREATE") {
                    selectedDropdownStyle()
                } else {
                    deselectedDropdownStyle()
                },
                spacing = Sizes.Icon.M
            )
            noteOptions.forEach { option ->
                DropdownItem(
                    label = option,
                    onClick = {
                        onNoteSelected(option)
                        isExpanded = false
                    },
                    textStyle = if (option == selectedNote) {
                        selectedDropdownStyle()
                    } else {
                        deselectedDropdownStyle()
                    },
                    spacing = Sizes.Icon.M
                )
            }
        }
    }
}
