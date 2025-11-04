package com.example.bghelp.utils

import android.content.Context
import java.io.File

class AudioManager(private val context: Context) {
    
    val recordingsDir: File
        get() = File(context.filesDir, "recordings").apply { mkdirs() }
    
    fun getDefaultAudioFiles(): List<String> {
        return context.assets.list("audio")?.toList()?.filter { 
            it.endsWith(".wav", ignoreCase = true) 
        } ?: emptyList()
    }
    
    fun getUserRecordings(): List<String> {
        return recordingsDir.listFiles()?.filter { 
            it.isFile && it.name.endsWith(".wav", ignoreCase = true) 
        }?.map { it.absolutePath } ?: emptyList()
    }
    
    fun getAudioFileLabel(fileName: String): String {
        return fileName.removeSuffix(".wav").removeSuffix(".WAV")
    }
    
    fun getAudioFileLabel(filePath: String, isFullPath: Boolean = false): String {
        return if (isFullPath) {
            File(filePath).nameWithoutExtension
        } else {
            getAudioFileLabel(filePath)
        }
    }
}

