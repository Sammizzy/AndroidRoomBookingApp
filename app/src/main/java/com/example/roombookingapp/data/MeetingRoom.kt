package com.example.roombookingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meeting_rooms")
data class MeetingRoom(
    @PrimaryKey(autoGenerate = true)
    val roomId: Int = 0,
    val roomName: String,
    val capacity: Int,
    val roomType: String, // e.g., Conference, Small, Training
    val description: String,
    val isActive: Boolean = true
)
