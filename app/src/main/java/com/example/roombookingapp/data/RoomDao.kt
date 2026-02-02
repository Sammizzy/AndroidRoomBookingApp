package com.example.roombookingapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomDao {
    @Query("SELECT * FROM meeting_rooms WHERE isActive = 1")
    suspend fun getAllActiveRooms(): List<MeetingRoom>

    @Query("SELECT * FROM meeting_rooms WHERE roomId = :roomId")
    suspend fun getRoomById(roomId: Int): MeetingRoom?

    @Insert
    suspend fun insertRoom(room: MeetingRoom)

    @Update
    suspend fun updateRoom(room: MeetingRoom)
}
