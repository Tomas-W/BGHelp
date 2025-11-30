package com.example.bghelp

import android.app.Application
import com.example.bghelp.utils.DatabaseInitializer
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BGHelpApp : Application() {
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // Initialize database defaults asynchronously
        applicationScope.launch {
            databaseInitializer.initialize()
        }
        
        // Key injected from env in gradle
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey.isNotBlank() && !Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }
}