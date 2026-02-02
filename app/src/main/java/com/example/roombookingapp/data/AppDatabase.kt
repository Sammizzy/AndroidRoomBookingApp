package com.example.roombookingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Booking::class, MeetingRoom::class, Equipment::class, User::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookingDao(): BookingDao
    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "room_booking_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val roomDao = getDatabase(context).roomDao()
                            for (i in 1..5) {
                                roomDao.insertRoom(
                                    MeetingRoom(
                                        roomName = "Meeting Room $i",
                                        capacity = 5 + i,
                                        roomType = if (i % 2 == 0) "Conference" else "Small",
                                        description = "Equipped with modern facilities for your meetings."
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
