package com.example.roombookingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roombookingapp.data.AppDatabase
import com.example.roombookingapp.data.BookingDao
import kotlinx.coroutines.launch

class ViewBookingsFragment : Fragment() {

    private lateinit var dao: BookingDao
    private lateinit var adapter: BookingAdapter
    private lateinit var rvBookings: RecyclerView
    private lateinit var tvEmptyMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = AppDatabase.getDatabase(requireContext()).bookingDao()
        
        rvBookings = view.findViewById(R.id.rvBookings)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)

        rvBookings.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = BookingAdapter(emptyList()) { booking ->
            viewLifecycleOwner.lifecycleScope.launch {
                dao.deleteBooking(booking)
                Toast.makeText(requireContext(), "Booking deleted", Toast.LENGTH_SHORT).show()
                refreshBookings()
            }
        }
        rvBookings.adapter = adapter

        refreshBookings()
    }

    private fun refreshBookings() {
        viewLifecycleOwner.lifecycleScope.launch {
            val bookings = dao.getAllBookings()
            if (bookings.isEmpty()) {
                tvEmptyMessage.visibility = View.VISIBLE
                rvBookings.visibility = View.GONE
            } else {
                tvEmptyMessage.visibility = View.GONE
                rvBookings.visibility = View.VISIBLE
                adapter.updateData(bookings)
            }
        }
    }
}
