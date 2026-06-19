package com.example.tutorialrun4.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tutorialrun4.helper.NotificationHelper
import com.example.tutorialrun4.helper.ReminderScheduler

class WateringWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val plantName = inputData.getString("plantName") ?: "your plant"
        val reminderId = inputData.getInt("reminderId", -1)
        val dayOfWeek = inputData.getInt("dayOfWeek", -1)
        val hour = inputData.getInt("hour", -1)
        val minute = inputData.getInt("minute", -1)

        if (reminderId != -1) {
            NotificationHelper.showNotification(applicationContext, plantName, reminderId)
            
            // Schedule the next occurrence (next week)
            if (dayOfWeek != -1 && hour != -1 && minute != -1) {
                ReminderScheduler.scheduleReminder(
                    context = applicationContext,
                    reminderId = reminderId,
                    plantName = plantName,
                    dayOfWeek = dayOfWeek,
                    hour = hour,
                    minute = minute
                )
            }
        }

        return androidx.work.ListenableWorker.Result.success()
    }
}
