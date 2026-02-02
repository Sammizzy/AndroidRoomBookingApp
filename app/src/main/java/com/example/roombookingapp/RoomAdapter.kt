package com.example.roombookingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roombookingapp.data.MeetingRoom

class RoomAdapter(
    private var rooms: List<MeetingRoom>,
    private val onBookClick: (MeetingRoom) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)
        val tvCapacity: TextView = view.findViewById(R.id.tvCapacity)
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
        
        holder.btnBook.setOnClickListener {
            onBookClick(room)
        }
    }

    override fun getItemCount(): Int = rooms.size

    fun updateData(newRooms: List<MeetingRoom>) {
        this.rooms = newRooms
        notifyDataSetChanged()
    }
}
