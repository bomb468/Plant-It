package com.example.tutorialrun4.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutorialrun4.repository.PlantData
import com.example.tutorialrun4.repository.PlantRepository
import com.example.tutorialrun4.repository.ReminderData
import com.example.tutorialrun4.room.Plant
import com.example.tutorialrun4.room.Reminder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class PlantScreenViewModel(
    private val plantRepository: PlantRepository,
    private val id: Long
) : ViewModel() {

    // Internal state for editing the plant
    var editedName by mutableStateOf("")
        private set
    
    var editedImageFile by mutableStateOf<File?>(null)
        private set

    // For new plants, we store reminders locally before saving
    val tempReminders = mutableStateListOf<ReminderData>()
    private var tempIdCounter = -1

    init {
        if (id != 0L) {
            viewModelScope.launch {
                val plant = plantRepository.getPlantById(id)
                plant?.let {
                    editedName = it.name
                    editedImageFile = it.imageName?.let { path -> if (path.isNotEmpty()) File(path) else null }
                }
            }
        }
    }

    // Expose a clean StateFlow of PlantData directly to the UI
    val plantState: StateFlow<PlantData> = combine(
        plantRepository.plantDataList,
        snapshotFlow { tempReminders.toList() }
    ) { allPlants, localReminders ->
        val plant = allPlants.find { it.id == id } ?: createDefaultPlant()
        
        if (id == 0L) {
            plant.copy(reminderList = localReminders)
        } else {
            plant
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = createDefaultPlant()
    )

    fun onNameChange(newName: String) {
        editedName = newName
    }

    fun onImageChange(file: File?) {
        editedImageFile = file
    }

    private fun createDefaultPlant(): PlantData {
        return PlantData(
            id = 0L,
            plantName = "",
            imageFile = null,
            reminderList = emptyList()
        )
    }

    fun savePlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val imageName = editedImageFile?.absolutePath
            if (id == 0L) {
                // Only save if name is not empty or if it's not just a default empty state
                if (editedName.isNotBlank() || editedImageFile != null || tempReminders.isNotEmpty()) {
                    val newId = plantRepository.addPlant(Plant(name = editedName, imageName = imageName))
                    // Save temp reminders for the new plant
                    tempReminders.forEach { reminder ->
                        plantRepository.addReminder(
                            Reminder(
                                plantId = newId,
                                hour = reminder.hour,
                                minute = reminder.minute,
                                dayOfWeek = reminder.dayOfWeek
                            )
                        )
                    }
                }
            } else {
                val existingPlant = plantRepository.getPlantById(id)
                if (existingPlant != null) {
                    plantRepository.updatePlant(existingPlant.copy(name = editedName, imageName = imageName))
                }
            }
            onSuccess()
        }
    }

    fun deletePlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existingPlant = plantRepository.getPlantById(id)
            if (existingPlant != null) {
                // Delete physical file if exists
                existingPlant.imageName?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.delete()
                }
                plantRepository.deletePlant(existingPlant)
            }
            onSuccess()
        }
    }

    fun addReminder(dayOfWeek: Int, hour: Int, minute: Int) {
        if (id == 0L) {
            tempReminders.add(ReminderData(id = tempIdCounter--, hour = hour, minute = minute, dayOfWeek = dayOfWeek))
        } else {
            viewModelScope.launch {
                plantRepository.addReminder(
                    Reminder(
                        plantId = id,
                        dayOfWeek = dayOfWeek,
                        hour = hour,
                        minute = minute
                    )
                )
            }
        }
    }

    fun deleteReminder(reminderId: Int) {
        if (id == 0L) {
            tempReminders.removeAll { it.id == reminderId }
        } else {
            viewModelScope.launch {
                plantRepository.deleteReminderById(reminderId)
            }
        }
    }
}
