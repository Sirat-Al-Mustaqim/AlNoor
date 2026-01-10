package com.siratalmustaqim.alnoor

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.siratalmustaqim.alnoor.worker.DnsEnforcementWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AlNoorApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Schedule DNS enforcement worker
        scheduleDnsEnforcementWork()
    }

    private fun scheduleDnsEnforcementWork() {
        val workRequest = PeriodicWorkRequestBuilder<DnsEnforcementWorker>(
            15, TimeUnit.MINUTES  // Minimum interval for periodic work
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DnsEnforcementWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Timber.d("DNS enforcement work scheduled")
    }
}
