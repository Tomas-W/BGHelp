package com.example.bghelp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun OptionsModal(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable (() -> Unit)? = null,
    firstOptionLabel: String,
    onFirstOption: () -> Unit,
    secondOptionLabel: String,
    onSecondOption: () -> Unit,
    thirdOptionLabel: String? = null,
    onThirdOption: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title)
                content?.let {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        it()
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onFirstOption()
                            onDismissRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = firstOptionLabel)
                    }
                    Button(
                        onClick = {
                            onSecondOption()
                            onDismissRequest()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = secondOptionLabel)
                    }
                    if (thirdOptionLabel != null && onThirdOption != null) {
                        Button(
                            onClick = {
                                onThirdOption()
                                onDismissRequest()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = thirdOptionLabel)
                        }
                    }
                }
            }
        }
    }
}

