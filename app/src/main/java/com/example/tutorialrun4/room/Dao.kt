package com.example.tutorialrun4.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    // Plant operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant): Long

    @Update
    suspend fun updatePlant(plant: Plant)

    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Long): Plant?

    @Delete
    suspend fun deletePlant(plant: Plant)

    // Reminder operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE plantId = :plantId")
    fun getRemindersForPlant(plantId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE plantId = :plantId")
    suspend fun getRemindersForPlantList(plantId: Long): List<Reminder>

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders")
    suspend fun getAllRemindersList(): List<Reminder>

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Int)
}
