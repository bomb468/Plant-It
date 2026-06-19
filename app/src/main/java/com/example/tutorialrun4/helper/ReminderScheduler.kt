package com.example.tutorialrun4.helper

import android.content.Context
import androidx.work.*
import com.example.tutorialrun4.worker.WateringWorker
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(context: Context, reminderId: Int, plantName: String, dayOfWeek: Int, hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(context)

        val delay = calculateDelay(dayOfWeek, hour, minute)

        val data = Data.Builder()
            .putString("plantName", plantName)
            .putInt("reminderId", reminderId)
            .putInt("dayOfWeek", dayOfWeek)
            .putInt("hour", hour)
            .putInt("minute", minute)
            .build()

        // We use a unique name for the work to avoid duplicates and allow cancellation
        val workRequest = OneTimeWorkRequestBuilder<WateringWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_$reminderId")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_work_$reminderId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(context: Context, reminderId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_work_$reminderId")
    }

    private fun calculateDelay(targetDayOfWeek: Int, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Set target time
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Adjust day of week
        // Calendar days: Sunday = 1, Monday = 2... Saturday = 7
        // Our input: Monday = 0, Sunday = 6
        val calendarTargetDay = when (targetDayOfWeek) {
            0 -> Calendar.MONDAY
            1 -> Calendar.TUESDAY
            2 -> Calendar.WEDNESDAY
            3 -> Calendar.THURSDAY
            4 -> Calendar.FRIDAY
            5 -> Calendar.SATURDAY
            6 -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }

        calendar.set(Calendar.DAY_OF_WEEK, calendarTargetDay)

        // If the target time is in the past (today or earlier this week), move to next week
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        return calendar.timeInMillis - now
    }
}
