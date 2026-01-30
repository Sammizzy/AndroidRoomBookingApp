package com.example.roombookingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roomId: Int,
    val roomName: String,
    val date: String,
    val time: String,
    val numberOfPeople: Int,
    val equipmentList: String
)
