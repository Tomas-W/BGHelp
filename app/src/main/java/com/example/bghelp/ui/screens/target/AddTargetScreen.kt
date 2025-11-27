package com.example.bghelp.ui.screens.target

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bghelp.ui.components.ButtonRow

@Composable
fun AddTargetScreen(
    viewModel: AddTargetViewModel,
    onTargetCreated: () -> Unit,
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Target Screen")
        }

        ButtonRow(
            modifier = Modifier.align(Alignment.BottomCenter),
            isValid = true,
            isLoading = false,
            firstLabel = "Save",
            firstOnClick = onTargetCreated,
            secondLabel = "Cancel",
            secondOnClick = { navController?.popBackStack() ?: onTargetCreated() }
        )
    }
}
