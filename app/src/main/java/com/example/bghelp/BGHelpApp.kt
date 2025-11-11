package com.example.bghelp

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BGHelpApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Key injected from env in gradle
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey.isNotBlank() && !Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }
}