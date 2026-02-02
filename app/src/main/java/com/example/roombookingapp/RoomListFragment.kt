package com.example.roombookingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roombookingapp.data.AppDatabase
import com.example.roombookingapp.data.BookingDao
import com.example.roombookingapp.data.MeetingRoom
import com.example.roombookingapp.data.RoomDao
import kotlinx.coroutines.launch

class RoomListFragment : Fragment() {

    private lateinit var roomDao: RoomDao
    private lateinit var bookingDao: BookingDao
    private lateinit var adapter: RoomAdapter
    private lateinit var rvRooms: RecyclerView
    private lateinit var tvEmptyMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        roomDao = db.roomDao()
        bookingDao = db.bookingDao()
        
        rvRooms = view.findViewById(R.id.rvRooms)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)

        rvRooms.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = RoomAdapter(emptyList()) { room ->
            val fragment = BookRoomFragment().apply {
                arguments = Bundle().apply {
                    putInt("roomId", room.roomId)
                    putString("roomName", room.roomName)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvRooms.adapter = adapter

        loadRooms()
    }

    private fun loadRooms() {
        viewLifecycleOwner.lifecycleScope.launch {
            var rooms = roomDao.getAllActiveRooms()

            // If still empty, try to populate manually as a fallback
            if (rooms.isEmpty()) {
                populateRoomsManually()
                rooms = roomDao.getAllActiveRooms()
            }

            val bookings = bookingDao.getAllBookings()

            if (rooms.isEmpty()) {
                tvEmptyMessage.visibility = View.VISIBLE
                rvRooms.visibility = View.GONE
            } else {
                tvEmptyMessage.visibility = View.GONE
                rvRooms.visibility = View.VISIBLE
                adapter.updateData(rooms, bookings)
            }
        }
    }

    private suspend fun populateRoomsManually() {
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
