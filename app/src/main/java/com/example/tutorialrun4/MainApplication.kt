package com.example.tutorialrun4

import android.app.Application
import com.example.tutorialrun4.helper.NotificationHelper
import com.example.tutorialrun4.koin.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Notification Channel for Watering Reminders
        NotificationHelper.createNotificationChannel(this)

        startKoin {
            androidContext(this@MainApplication)
            modules(mainModule)
        }
    }
}