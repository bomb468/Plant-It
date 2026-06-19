package com.example.tutorialrun4.repository

import android.content.Context
import com.example.tutorialrun4.helper.ReminderScheduler
import com.example.tutorialrun4.room.Plant
import com.example.tutorialrun4.room.PlantDao
import com.example.tutorialrun4.room.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.io.File

data class PlantData(
    val id : Long,
    val plantName : String,
    val imageFile : File? = null,
    val reminderList : List<ReminderData>
)
data class ReminderData(
    val id: Int = 0,
    val hour : Int,
    val minute : Int,
    val dayOfWeek : Int
)
class PlantRepository(
    private val plantDao: PlantDao,
    private val context: Context
) {

    // Expose a unified Flow of your UI-ready data model
    val plantDataList: Flow<List<PlantData>> = combine(
        plantDao.getAllPlants(),
        plantDao.getAllReminders()
    ) { plants, reminders ->
        plants.map { plant ->
            val plantReminders = reminders
                .filter { it.plantId == plant.id }
                .map { ReminderData(it.id, it.hour, it.minute, it.dayOfWeek) }

            PlantData(
                id = plant.id,
                plantName = plant.name,
                imageFile = File(plant.imageName ?: ""),
                reminderList = plantReminders
            )
        }
    }

    suspend fun addPlant(plant: Plant): Long {
        return plantDao.insertPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.insertPlant(plant)
    }

    suspend fun deletePlant(plant: Plant) {
        // Cancel all reminders for this plant before deleting
        val reminders = plantDao.getRemindersForPlantList(plant.id)
        reminders.forEach {
            ReminderScheduler.cancelReminder(context, it.id)
        }
        plantDao.deletePlant(plant)
    }

    suspend fun getPlantById(id: Long): Plant? {
        return plantDao.getPlantById(id)
    }

    suspend fun addReminder(reminder: Reminder): Long {
        val id = plantDao.insertReminder(reminder)
        val plant = plantDao.getPlantById(reminder.plantId)
        if (plant != null) {
            ReminderScheduler.scheduleReminder(
                context = context,
                reminderId = id.toInt(),
                plantName = plant.name,
                dayOfWeek = reminder.dayOfWeek,
                hour = reminder.hour,
                minute = reminder.minute
            )
        }
        return id
    }

    suspend fun deleteReminderById(id: Int) {
        ReminderScheduler.cancelReminder(context, id)
        plantDao.deleteReminderById(id)
    }
    
    suspend fun rescheduleAllReminders() {
        val allReminders = plantDao.getAllRemindersList()
        val allPlants = plantDao.getAllPlants().first()
        
        allReminders.forEach { reminder ->
            val plant = allPlants.find { it.id == reminder.plantId }
            if (plant != null) {
                ReminderScheduler.scheduleReminder(
                    context = context,
                    reminderId = reminder.id,
                    plantName = plant.name,
                    dayOfWeek = reminder.dayOfWeek,
                    hour = reminder.hour,
                    minute = reminder.minute
                )
            }
        }
    }
}
