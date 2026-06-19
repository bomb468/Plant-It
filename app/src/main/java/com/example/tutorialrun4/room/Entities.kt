package com.example.tutorialrun4.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val imageName: String? = null
)

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: Long,
    val hour: Int, // 0-23
    val minute: Int, // 0-59
    val dayOfWeek: Int // e.g., 0 for Monday, 6 for Sunday (standard Calendar/java.time values)
)
