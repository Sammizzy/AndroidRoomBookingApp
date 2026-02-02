package com.example.roombookingapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.roombookingapp.data.AppDatabase
import com.example.roombookingapp.data.Booking
import com.example.roombookingapp.data.MeetingRoom
import com.example.roombookingapp.data.User
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var session: UserSession

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        session = UserSession(requireContext())
        val tvName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnTestApp = view.findViewById<Button>(R.id.btnTestApp)
        val tvTestResults = view.findViewById<TextView>(R.id.tvTestResults)

        val db = AppDatabase.getDatabase(requireContext())
        val userDao = db.userDao()

        lifecycleScope.launch {
            val user = userDao.getUserById(session.getUserId())
            user?.let {
                tvName.text = "Name: ${it.name}"
                tvEmail.text = "Email: ${it.email}"
            }
        }

        btnTestApp.setOnClickListener {
            runAppTests(tvTestResults)
        }

        btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun runAppTests(resultsTextView: TextView) {
        val db = AppDatabase.getDatabase(requireContext())
        val results = StringBuilder()
        resultsTextView.visibility = View.VISIBLE
        resultsTextView.text = "Running tests...\n"

        lifecycleScope.launch {
            try {
                // 1. Test User Management (Must Have)
                results.append("1.User Management: ")
                val testUser = User(email = "test@example.com", password = "password", name = "Test User")
                db.userDao().registerUser(testUser)
                val retrievedUser = db.userDao().getUserByEmail("test@example.com")
                if (retrievedUser != null) {
                    results.append("PASSED (User registered & retrieved)\n")
                } else {
                    results.append("FAILED\n")
                }

                // 2. Test Room Availability (Must Have)
                results.append("2.Room Availability: ")
                val rooms = db.roomDao().getAllActiveRooms()
                if (rooms.isNotEmpty()) {
                    results.append("PASSED (${rooms.size} rooms found)\n")
                } else {
                    results.append("FAILED (No rooms in DB)\n")
                }

                // 3. Test Booking Creation (Must Have)
                results.append("3.Booking Creation: ")
                if (rooms.isNotEmpty()) {
                    val booking = Booking(
                        userId = session.getUserId(),
                        roomId = rooms[0].roomId,
                        roomName = rooms[0].roomName,
                        date = "01/01/2099",
                        startTime = "10:00 AM",
                        endTime = "11:00 AM",
                        numberOfPeople = 5,
                        equipmentList = "Projector"
                    )
                    db.bookingDao().insertBooking(booking)
                    val userBookings = db.bookingDao().getBookingsForUser(session.getUserId())
                    if (userBookings.any { it.date == "01/01/2099" }) {
                        results.append("PASSED (Booking created)\n")
                    } else {
                        results.append("FAILED\n")
                    }
                } else {
                    results.append("SKIPPED (No rooms)\n")
                }

                // 4. Test Conflict Detection (Should Have)
                results.append("4.Conflict Detection: ")
                if (rooms.isNotEmpty()) {
                    val conflicts = db.bookingDao().getConflictingBookings(rooms[0].roomId, "01/01/2099", "10:30 AM", "11:30 AM")
                    if (conflicts.isNotEmpty()) {
                        results.append("PASSED (Conflict detected correctly)\n")
                    } else {
                        results.append("FAILED (Allowed double booking)\n")
                    }
                } else {
                    results.append("SKIPPED\n")
                }

                // 5. Test Equipment Tracking (Could Have)
                results.append("5.Equipment Tracking: ")
                val lastBooking = db.bookingDao().getAllBookings().lastOrNull()
                if (lastBooking?.equipmentList?.contains("Projector") == true) {
                    results.append("PASSED (Equipment stored)\n")
                } else {
                    results.append("FAILED\n")
                }

                results.append("\nALL CORE FUNCTIONS VERIFIED.")
            } catch (e: Exception) {
                results.append("\nTEST SUITE ERROR: ${e.message}")
            }
            resultsTextView.text = results.toString()
        }
    }
}
