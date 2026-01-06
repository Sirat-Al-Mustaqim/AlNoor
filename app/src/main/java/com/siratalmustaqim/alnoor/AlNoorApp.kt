package com.siratalmustaqim.alnoor

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AlNoorApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            // Plant a debug tree for debug builds
            Timber.plant(Timber.DebugTree())
        }
        // In release builds, no tree is planted, so no logs will be output
    }
}
