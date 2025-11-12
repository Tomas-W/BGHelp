package com.example.bghelp.utils

import android.content.Context
import android.os.Environment
import java.io.File

object TaskImageStorage {
    private const val TASK_IMAGE_DIRECTORY = "tasks"

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
}

