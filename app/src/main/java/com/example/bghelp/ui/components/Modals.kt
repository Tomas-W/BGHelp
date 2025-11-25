package com.example.bghelp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bghelp.ui.theme.Sizes

@Composable
fun OptionsModal(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String? = null,
    description: String? = null,
    content: @Composable (() -> Unit)? = null,
    cancelLabel: String? = null,
    cancelOption: (() -> Unit)? = null,
    confirmLabel: String,
    confirmOption: () -> Unit,
    extraLabel: String? = null,
    extraOption: (() -> Unit)? = null
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Sizes.Corner.M))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(Sizes.Icon.S),
            ) {
                // Title
                if (title != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Description
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                // Composable
                if (content != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        content()
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (extraLabel != null && extraOption != null) {
                        ConfirmButton(
                            modifier = Modifier.weight(1f),
                            text = extraLabel,
                            onClick = {
                                extraOption()
                                onDismissRequest()
                            }
                        )
                    }

                    if (cancelLabel != null && cancelOption != null) {
                        CancelButton(
                            modifier = Modifier.weight(1f),
                            text = cancelLabel,
                            onClick = {
                                cancelOption()
                                onDismissRequest()
                            }
                        )
                    }

                    ConfirmButton(
                        modifier = Modifier.weight(1f),
                        text = confirmLabel,
                        onClick = {
                            confirmOption()
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}
