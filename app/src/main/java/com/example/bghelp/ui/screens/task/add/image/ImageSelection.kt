package com.example.bghelp.ui.screens.task.add.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bghelp.R
import com.example.bghelp.ui.components.CustomDropdown
import com.example.bghelp.ui.components.DropdownItem
import com.example.bghelp.ui.components.deselectedDropdownStyle
import com.example.bghelp.ui.screens.task.add.TaskImageData
import com.example.bghelp.ui.theme.Sizes
import com.example.bghelp.ui.theme.lTextDefault
import com.example.bghelp.ui.utils.clickableDismissFocus
import com.example.bghelp.ui.screens.task.add.AddTaskConstants as CONST

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
    val imageLabel = selectedImage?.displayName ?: stringResource(R.string.task_no_image_selected)

    Column(
        verticalArrangement = Arrangement.spacedBy(Sizes.Size.S),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            Text(
                modifier = Modifier
                    .clickableDismissFocus { onAddImageClick() },
                text = stringResource(R.string.task_add_image),
                style = MaterialTheme.typography.lTextDefault
            )

            CustomDropdown(
                expanded = isMenuExpanded,
                onDismissRequest = onDismissMenu
            ) {
                DropdownItem(
                    label = stringResource(R.string.task_select_library),
                    onClick = {
                        onSelectFromLibrary()
                        onDismissMenu()
                    },
                    textStyle = deselectedDropdownStyle(),
                    spacing = Sizes.Icon.S
                )
                DropdownItem(
                    label = stringResource(R.string.task_capture_camera),
                    onClick = {
                        onCapturePhoto()
                        onDismissMenu()
                    },
                    textStyle = deselectedDropdownStyle(),
                    spacing = Sizes.Icon.M
                )
            }
        }
        if (!hasImage) {
            Text(
                text = imageLabel,
                style = MaterialTheme.typography.lTextDefault
            )
        }


        previewImage?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = imageLabel,
                modifier = Modifier
                    .size(CONST.IMAGE_SIZE.dp)
                    .clip(RoundedCornerShape(Sizes.Corner.S)),
                contentScale = ContentScale.Crop
            )
        }
    }
}