package com.example.roombookingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val roomId: Int,
    val roomName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val numberOfPeople: Int,
    val equipmentList: String,
    val status: String = "CONFIRMED" // CONFIRMED, CANCELLED
)
