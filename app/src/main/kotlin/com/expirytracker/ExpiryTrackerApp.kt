package com.expirytracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.expirytracker.data.worker.ExpiryReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ExpiryTrackerApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleExpiryReminders()
    }

    private fun scheduleExpiryReminders() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<ExpiryReminderWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ExpiryReminders",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
