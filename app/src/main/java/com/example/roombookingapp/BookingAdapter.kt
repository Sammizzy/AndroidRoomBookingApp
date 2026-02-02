package com.example.roombookingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roombookingapp.data.Booking

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onDeleteClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvRoomName.text = booking.roomName
        holder.tvDateTime.text = "${booking.date} at ${booking.startTime} - ${booking.endTime}"
        holder.tvDetails.text = "People: ${booking.numberOfPeople} | Equipment: ${booking.equipmentList.ifBlank { "None" }}"
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(booking)
        }
    }

    override fun getItemCount(): Int = bookings.size

    fun updateData(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }
}
