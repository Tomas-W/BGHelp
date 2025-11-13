package com.example.bghelp.ui.screens.task.add.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.theme.TextStyles

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
    val displayText = remember(selectedNote) {
        selectedNote.ifEmpty {
            STR.SELECT_NOTE
        }
    }

    Box(
        modifier = modifier
            .wrapContentWidth()
    ) {
        Text(
            text = displayText,
            style = deselectedStyle,
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
                label = STR.SELECT_NOTE,
                onClick = {
                    onNoteSelected("")
                    isExpanded = false
                },
                textStyle = TextStyles.Default.S
            )
            DropdownItem(
                label = STR.CREATE_NOTE,
                onClick = {
                    onNoteSelected("CREATE")
                    isExpanded = false
                },
                textStyle = TextStyles.Default.S
            )
            noteOptions.forEach { option ->
                DropdownItem(
                    label = option,
                    onClick = {
                        onNoteSelected(option)
                        isExpanded = false
                    },
                    textStyle = TextStyles.Default.S
                )
            }
        }
    }
}
