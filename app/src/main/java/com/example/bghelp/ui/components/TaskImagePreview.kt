package com.example.bghelp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.utils.TaskImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TaskImagePreview(
    imagePath: String,
    displayName: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val previewImage by produceState<ImageBitmap?>(initialValue = null, imagePath) {
        value = withContext(Dispatchers.IO) {
            val file = TaskImageStorage.resolveFile(context, imagePath)
            if (!file.exists()) {
                return@withContext null
            }
            BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
        }
    }

    var showModal by rememberSaveable { mutableStateOf(false) }

    if (previewImage != null) {
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
                Spacer(modifier = Modifier.height(Sizes.Size.ExtraSmall))
            }
            val painter = remember(previewImage) { BitmapPainter(previewImage!!) }
            Image(
                painter = painter,
                contentDescription = displayName ?: "Task image preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Sizes.Corner.Small))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { showModal = true })
                    }
            )
        }
    }

    if (showModal && previewImage != null) {
        TaskImageModal(
            image = previewImage!!,
            displayName = displayName,
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskImageModal(
    image: ImageBitmap,
    displayName: String?,
    onDismiss: () -> Unit
) {
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
                    contentDescription = "Close image",
                    tint = Color.White
                )
            }

            var scale by remember { mutableStateOf(1f) }
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

            val painter = remember(image) { BitmapPainter(image) }

            Image(
                painter = painter,
                contentDescription = displayName ?: "Task image",
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

