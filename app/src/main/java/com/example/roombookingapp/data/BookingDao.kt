package com.example.roombookingapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getBookingsForUser(userId: Int): List<Booking>

    @Query("SELECT * FROM bookings")
    suspend fun getAllBookings(): List<Booking>

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("""
        SELECT * FROM bookings 
        WHERE roomId = :roomId 
        AND date = :date 
        AND status = 'CONFIRMED'
        AND (
            (startTime < :endTime AND endTime > :startTime)
        )
    """)
    suspend fun getConflictingBookings(roomId: Int, date: String, startTime: String, endTime: String): List<Booking>
}
