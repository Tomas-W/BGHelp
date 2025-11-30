package com.example.bghelp.ui.screens.task.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bghelp.R
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.utils.TaskImageStorage
import java.io.File

@Composable
fun TaskImagePreview(
    imagePath: String,
    displayName: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageFile = remember(imagePath) {
        TaskImageStorage.resolveFile(context, imagePath)
    }
    
    var showModal by rememberSaveable { mutableStateOf(false) }

    if (imageFile.exists()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            displayName?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Sizes.Size.XS))
            }
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageFile)
                    .crossfade(true)
                    .build(),
                contentDescription = displayName ?: stringResource(R.string.task_image_preview),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Sizes.Corner.S))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { 
                            // Load image for modal
                            showModal = true
                        })
                    }
            )
        }
    }

    if (showModal) {
        TaskImageModal(
            imageFile = imageFile,
            displayName = displayName,
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskImageModal(
    imageFile: File,
    displayName: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.task_close_image),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            val transformState = rememberTransformableState { zoomChange, panChange, _ ->
                val newScale = (scale * zoomChange).coerceIn(1f, 4f)
                val appliedPan = if (scale == 1f && newScale == 1f) {
                    Offset.Zero
                } else {
                    panChange
                }
                scale = newScale
                offset += appliedPan
            }

            // Use AsyncImage with transformable gestures
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageFile)
                    .build(),
                contentDescription = displayName ?: stringResource(R.string.task_task_image),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        )
                    }
                    .transformable(transformState)
            )
        }
    }
}

