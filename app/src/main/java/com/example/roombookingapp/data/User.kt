package com.example.roombookingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val role: String = "USER" // USER or ADMIN
)
