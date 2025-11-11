package com.example.bghelp.ui.screens.task.add.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST
import com.example.bghelp.ui.screens.task.add.AddTaskStrings as STR
import com.example.bghelp.ui.screens.task.add.TaskImageData
import com.example.bghelp.ui.screens.task.add.deselectedStyle
import com.example.bghelp.ui.screens.task.add.selectedStyle
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.utils.clickableDismissFocus

@Composable
fun ImageSelection(
    selectedImage: TaskImageData?,
    previewImage: ImageBitmap?,
    isMenuExpanded: Boolean,
    onAddImageClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onSelectFromLibrary: () -> Unit,
    onCapturePhoto: () -> Unit
) {
    val hasImage = selectedImage != null
    val imageLabel = selectedImage?.displayName ?: STR.NO_IMAGE_SELECTED

    Column(
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.Small),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            Text(
                modifier = Modifier
                    .clickableDismissFocus { onAddImageClick() },
                text = STR.ADD_IMAGE,
                style = deselectedStyle
            )

            CustomDropdown(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu
            ) {
                DropdownItem(
                    label = STR.IMAGE_FROM_LIBRARY,
                    onClick = {
                        onSelectFromLibrary()
                        onDismissMenu()
                    },
                    textStyle = deselectedStyle
                )
                DropdownItem(
                    label = STR.IMAGE_FROM_CAMERA,
                    onClick = {
                        onCapturePhoto()
                        onDismissMenu()
                    },
                    textStyle = deselectedStyle
                )
            }
        }

        Text(
            text = imageLabel,
            style = if (hasImage) {
                selectedStyle
            } else {
                deselectedStyle
            }
        )

        previewImage?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = imageLabel,
                modifier = Modifier
                    .size(CONST.IMAGE_SIZE.dp)
                    .clip(RoundedCornerShape(Sizes.Corner.Small)),
                contentScale = ContentScale.Crop
            )
        }
    }
}