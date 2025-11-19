package com.example.bghelp.ui.screens.task.add.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.AddTaskViewModel
import com.example.bghelp.ui.screens.task.add.Header
import com.example.bghelp.ui.screens.task.add.SubContainer
import com.example.bghelp.ui.screens.task.add.UserImageSelection
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Image(
    viewModel: AddTaskViewModel
) {
    val context = LocalContext.current
    val userImageSelection = viewModel.userImageSelection.collectAsState().value
    val selectedImageState = viewModel.selectedImage.collectAsState()
    val selectedImageData = selectedImageState.value

    var isMenuExpanded by remember { mutableStateOf(false) }
    var launchCamera by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        isMenuExpanded = false
        uri?.let {
            val displayName = resolveDisplayName(context, it)
            viewModel.setImageFromGallery(it, displayName)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        launchCamera = false
        isMenuExpanded = false
        bitmap?.let {
            viewModel.setImageFromCamera(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera = true
        } else {
            Toast.makeText(
                context,
                STR.CAMERA_PERMISSION_REQUIRED,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(launchCamera) {
        if (launchCamera) {
            cameraLauncher.launch(null)
        }
    }

    val previewImage by produceState<ImageBitmap?>(initialValue = null, selectedImageData) {
        val imageData = selectedImageData
        value = when {
            imageData == null -> null
            // URI (gallery or temporary camera file)
            imageData.uri != null -> loadImageBitmap(context, imageData.uri)
            else -> null
        }
    }

    Header(
        viewModel = viewModel,
        userSectionSelection = userImageSelection,
        toggleSection = { viewModel.toggleImageSelection() }
    )

    if (userImageSelection == UserImageSelection.ON) {
        SubContainer {
            ImageSelection(
                selectedImage = selectedImageData,
                previewImage = previewImage,
                isMenuExpanded = isMenuExpanded,
                onAddImageClick = {
                    viewModel.clearAllInputSelections()
                    isMenuExpanded = true
                },
                onDismissMenu = { isMenuExpanded = false },
                onSelectFromLibrary = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onCapturePhoto = {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasCameraPermission) {
                        launchCamera = true
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )
        }
    }
}

private suspend fun loadImageBitmap(
    context: Context,
    uri: Uri
): ImageBitmap? = withContext(Dispatchers.IO) {
    try {
        val bitmap = when {
            // Handle file:// URIs (temporary camera files)
            uri.scheme == "file" -> {
                val file = java.io.File(uri.path ?: return@withContext null)
                if (!file.exists()) return@withContext null
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(file)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                }
            }
            // Handle content:// URIs (gallery)
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            }
        }
        bitmap?.asImageBitmap()
    } catch (ioException: IOException) {
        null
    } catch (e: Exception) {
        null
    }
}

private fun resolveDisplayName(
    context: Context,
    uri: Uri
): String? {
    return context.contentResolver
        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                cursor.getString(columnIndex)
            } else {
                null
            }
        }
}
