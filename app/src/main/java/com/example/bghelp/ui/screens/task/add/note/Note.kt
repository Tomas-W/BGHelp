package com.example.bghelp.ui.screens.task.add.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.UserNoteSelection

@Composable
fun Note(
    viewModel: AddTaskViewModel,
    navController: NavController
) {
    val userNoteSelection by viewModel.userNoteSelection.collectAsState()
    val selectedNote by viewModel.selectedNote.collectAsState()

    Header(
        viewModel = viewModel,
        userSectionSelection = userNoteSelection,
        toggleSection = { viewModel.toggleNoteSelection() }
    )

    if (userNoteSelection == UserNoteSelection.ON) {
        SubContainer {
            NoteSelection(
                selectedNote = selectedNote,
                onNoteSelected = { note ->
                    viewModel.setSelectedNote(note)
                },
                navController = navController
            )
        }
    }
}
