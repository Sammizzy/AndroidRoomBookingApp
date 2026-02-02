package com.example.roombookingapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roombookingapp.data.Booking
import com.example.roombookingapp.data.MeetingRoom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RoomAdapter(
    private var rooms: List<MeetingRoom>,
    private var currentBookings: List<Booking> = emptyList(),
    private val onBookClick: (MeetingRoom) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)
        val tvCapacity: TextView = view.findViewById(R.id.tvCapacity)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnBook: Button = view.findViewById(R.id.btnBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.tvRoomName.text = room.roomName
        holder.tvCapacity.text = "Capacity: ${room.capacity}"
        holder.tvDescription.text = room.description
        
        val isBooked = isRoomCurrentlyBooked(room.roomId)
        if (isBooked) {
            holder.tvStatus.text = "In Use"
            holder.tvStatus.setTextColor(Color.RED)
            holder.btnBook.isEnabled = false
            holder.btnBook.text = "Unavailable"
        } else {
            holder.tvStatus.text = "Available"
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            holder.btnBook.isEnabled = true
            holder.btnBook.text = "Book Now"
        }
        
        holder.btnBook.setOnClickListener {
            onBookClick(room)
        }
    }

    private fun isRoomCurrentlyBooked(roomId: Int): Boolean {
        val now = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        val currentDate = dateFormat.format(now)
        val currentTimeStr = timeFormat.format(now)
        val currentTime = timeFormat.parse(currentTimeStr)

        return currentBookings.any { booking ->
            if (booking.roomId == roomId && booking.date == currentDate && booking.status == "CONFIRMED") {
                try {
                    val start = timeFormat.parse(booking.startTime)
                    val end = timeFormat.parse(booking.endTime)
                    currentTime != null && start != null && end != null && 
                            currentTime.after(start) && currentTime.before(end)
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = rooms.size

    fun updateData(newRooms: List<MeetingRoom>, newBookings: List<Booking>) {
        this.rooms = newRooms
        this.currentBookings = newBookings
        notifyDataSetChanged()
    }
}
