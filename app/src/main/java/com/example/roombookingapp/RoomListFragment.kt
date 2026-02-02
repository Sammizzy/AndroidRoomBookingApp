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
import com.example.roombookingapp.data.RoomDao
import kotlinx.coroutines.launch

class RoomListFragment : Fragment() {

    private lateinit var roomDao: RoomDao
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

        roomDao = AppDatabase.getDatabase(requireContext()).roomDao()
        
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
            val rooms = roomDao.getAllActiveRooms()
            if (rooms.isEmpty()) {
                tvEmptyMessage.visibility = View.VISIBLE
                rvRooms.visibility = View.GONE
            } else {
                tvEmptyMessage.visibility = View.GONE
                rvRooms.visibility = View.VISIBLE
                adapter.updateData(rooms)
            }
        }
    }
}
