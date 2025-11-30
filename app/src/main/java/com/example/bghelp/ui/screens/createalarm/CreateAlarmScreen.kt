package com.example.bghelp.ui.screens.createalarm

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.bghelp.ui.components.MainContentContainer

@Composable
fun CreateAlarmScreen(
    viewModel: CreateAlarmViewModel = hiltViewModel()
) {
    MainContentContainer {
        Text("This is the create alarm screen")
    }
}
