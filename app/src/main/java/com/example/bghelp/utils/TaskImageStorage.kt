package com.example.bghelp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.example.bghelp.domain.model.TaskImageAttachment
import com.example.bghelp.domain.model.TaskImageSourceOption
import com.example.bghelp.ui.screens.task.add.AddTaskStrings
import com.example.bghelp.ui.screens.task.add.TaskImageData
import com.example.bghelp.ui.screens.task.add.TaskImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

object TaskImageStorage {
    private const val TASK_IMAGE_DIRECTORY = "tasks"
    private const val TEMP_IMAGE_DIRECTORY = "temp"

    suspend fun persistIfNeeded(
        context: Context,
        isOn: Boolean,
        selected: TaskImageData?
    ): TaskImageAttachment? {
        if (!isOn) return null
        val image = selected ?: return null
        return persistTaskImage(context, image)
    }

    // Temporary save image before saving the Task
    suspend fun saveTemporaryImage(
        context: Context,
        bitmap: Bitmap,
        displayName: String
    ): Uri = withContext(Dispatchers.IO) {
        val tempDir = tempImageDirectory(context)
        val extension = resolveExtensionFromDisplayName(displayName)
        val fileName = "${UUID.randomUUID()}.$extension"
        val tempFile = File(tempDir, fileName)

        FileOutputStream(tempFile).use { stream ->
            val format = extension.toCompressFormat()
            val success = bitmap.compress(format, 92, stream)
            if (!success) {
                // Clean up partial file before throwing
                tempFile.delete()
                throw IllegalStateException("Unable to save temporary image: compression failed.")
            }
        }

        // Create URI
        Uri.fromFile(tempFile)
    }

    private fun tempImageDirectory(context: Context): File {
        val directory = File(imageRoot(context), TEMP_IMAGE_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun cleanupTemporaryImages(context: Context) {
        val tempDir = File(imageRoot(context), TEMP_IMAGE_DIRECTORY)
        if (tempDir.exists()) {
            tempDir.listFiles()?.forEach { it.delete() }
        }
    }

    fun isTemporaryUri(uri: Uri): Boolean {
        return uri.scheme == "file" && uri.path?.contains(TEMP_IMAGE_DIRECTORY) == true
    }

    private fun resolveExtensionFromDisplayName(displayName: String): String {
        val fromName = displayName.substringAfterLast(".", "")
        val normalizedName = normalizeExtension(fromName)
        return normalizedName ?: "jpg"
    }

    fun imageRoot(context: Context): File {
        val externalRoot = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val root = externalRoot ?: File(context.filesDir, Environment.DIRECTORY_PICTURES)
        if (!root.exists()) {
            root.mkdirs()
        }
        return root
    }

    fun taskImageDirectory(context: Context): File {
        val directory = File(imageRoot(context), TASK_IMAGE_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun buildRelativePath(fileName: String): String =
        "$TASK_IMAGE_DIRECTORY/$fileName"

    fun resolveFile(context: Context, relativePath: String): File =
        File(imageRoot(context), relativePath)

    fun deleteImage(context: Context, relativePath: String) {
        val file = resolveFile(context, relativePath)
        if (file.exists()) {
            file.delete()
        }
    }

    suspend fun persistTaskImage(
        context: Context,
        imageData: TaskImageData
    ): TaskImageAttachment? {
        val source = imageData.source.toDomainSource() ?: return null
        return withContext(Dispatchers.IO) {
            val directory = taskImageDirectory(context)
            val extension = resolveImageExtension(context, imageData)
            val fileName = "${UUID.randomUUID()}.$extension"
            val targetFile = File(directory, fileName)

            try {
                when {
                    // URI (gallery or temporary camera file)
                    imageData.uri != null -> {
                        val sourceFile = if (isTemporaryUri(imageData.uri)) {
                            // Temporary file: move it to permanent location
                            File(imageData.uri.path ?: throw IllegalStateException("Invalid temporary file URI"))
                        } else {
                            // Gallery URI: copy it
                            null
                        }

                        if (sourceFile != null && sourceFile.exists()) {
                            // Move temporary file to permanent location
                            sourceFile.copyTo(targetFile, overwrite = true)
                            sourceFile.delete() // Clean up temporary file
                        } else {
                            // Copy from URI stream (gallery)
                            context.contentResolver.openInputStream(imageData.uri)?.use { input ->
                                FileOutputStream(targetFile).use { output ->
                                    input.copyTo(output)
                                }
                            } ?: throw IllegalStateException("Unable to open selected image stream.")
                        }
                    }
                    else -> return@withContext null
                }

                val storedDisplayName = imageData.displayName
                    .takeUnless { it.isBlank() || it == AddTaskStrings.NO_IMAGE_SELECTED }
                TaskImageAttachment(
                    uri = buildRelativePath(targetFile.name),
                    displayName = storedDisplayName,
                    source = source
                )
            } catch (throwable: Throwable) {
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                throw throwable
            }
        }
    }

    private fun resolveImageExtension(context: Context, image: TaskImageData): String {
        val fromMime = image.uri?.let { uri ->
            context.contentResolver.getType(uri)?.substringAfterLast("/")
        }
        val normalizedMime = fromMime?.let(::normalizeExtension)
        if (normalizedMime != null) return normalizedMime

        val fromName = image.displayName.substringAfterLast(".", "")
        val normalizedName = normalizeExtension(fromName)
        if (normalizedName != null) return normalizedName

        return "jpg"
    }

    private fun normalizeExtension(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned = raw.lowercase(Locale.getDefault())
        return when (cleaned) {
            "jpeg" -> "jpg"
            "jpg" -> "jpg"
            "png" -> "png"
            "webp" -> "webp"
            else -> null
        }
    }

    private fun String.toCompressFormat(): Bitmap.CompressFormat = when (lowercase(Locale.getDefault())) {
        "png" -> Bitmap.CompressFormat.PNG
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }

    private fun TaskImageSource.toDomainSource(): TaskImageSourceOption? = when (this) {
        TaskImageSource.GALLERY -> TaskImageSourceOption.GALLERY
        TaskImageSource.CAMERA -> TaskImageSourceOption.CAMERA
    }
}

