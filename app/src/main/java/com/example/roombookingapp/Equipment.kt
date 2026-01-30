package com.example.roombookingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true)
    val equipmentId: Int = 0,
    val name: String
)
