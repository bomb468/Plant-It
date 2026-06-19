package com.example.tutorialrun4.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.tutorialrun4.repository.PlantData
import com.example.tutorialrun4.repository.ReminderData
import com.example.tutorialrun4.viewmodels.PlantListScreenViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun PlantListScreen(
    onPlantClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val plantListScreenViewModel = koinViewModel<PlantListScreenViewModel>()
    val plantList by plantListScreenViewModel.plantList.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onPlantClick(0) },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                text = { Text("Add Plant") }
            )
        }
    ) { paddingValues ->
        if (plantList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No plants added yet!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tap + to start your garden",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        } else {
            PlantList(
                plantList = plantList,
                onPlantClick = onPlantClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
fun PlantList(
    plantList: List<PlantData>,
    onPlantClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = plantList,
            key = { plantData -> plantData.id }
        ) { plantData ->
            PlantListItem(
                plantData = plantData,
                onClick = { onPlantClick(plantData.id) }
            )
        }
    }
}

@Composable
fun PlantListItem(
    plantData: PlantData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plant Image Box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (plantData.imageFile?.exists() == true && plantData.imageFile.length() > 0) {
                    AsyncImage(
                        model = plantData.imageFile,
                        contentDescription = "Plant image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Fallback placeholder",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plantData.plantName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                val nextReminderText = getNextReminderText(plantData.reminderList)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (nextReminderText.contains("Today")) Color(0xFF2196F3) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = nextReminderText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (nextReminderText.contains("Today")) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                val activeDays = plantData.reminderList.map { it.dayOfWeek }.distinct()
                WateringScheduleRow(wateringDays = activeDays)
            }
        }
    }
}

@Composable
fun WateringScheduleRow(
    wateringDays: List<Int>,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        daysOfWeek.forEachIndexed { index, dayName ->
            val isWaterDay = wateringDays.contains(index)

            val backgroundColor = if (isWaterDay) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }

            val textColor = if (isWaterDay) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayName,
                    fontSize = 10.sp,
                    fontWeight = if (isWaterDay) FontWeight.Bold else FontWeight.Normal,
                    color = textColor
                )
            }
        }
    }
}

fun getNextReminderText(reminders: List<ReminderData>): String {
    if (reminders.isEmpty()) return "No reminders set"
    
    val now = Calendar.getInstance()
    val currentDay = (now.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Convert to 0=Mon, 6=Sun
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(Calendar.MINUTE)

    val sortedReminders = reminders.sortedWith(compareBy({ it.dayOfWeek }, { it.hour }, { it.minute }))

    var next = sortedReminders.find { 
        it.dayOfWeek == currentDay && (it.hour > currentHour || (it.hour == currentHour && it.minute > currentMinute))
    }

    if (next == null) {
        next = sortedReminders.find { it.dayOfWeek > currentDay }
    }

    if (next == null) {
        next = sortedReminders.first()
    }

    val daysOfWeekNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayName = if (next.dayOfWeek == currentDay) "Today" else daysOfWeekNames[next.dayOfWeek]
    val amPm = if (next.hour < 12) "AM" else "PM"
    val displayHour = if (next.hour == 0) 12 else if (next.hour > 12) next.hour - 12 else next.hour
    
    val timeFormatted = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, next.minute, amPm)
    return "Next: $dayName $timeFormatted"
}
