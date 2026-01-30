package com.example.roombookingapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings")
    suspend fun getAllBookings(): List<Booking>

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
