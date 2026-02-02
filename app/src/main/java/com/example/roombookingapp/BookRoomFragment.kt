package com.example.roombookingapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.roombookingapp.data.AppDatabase
import com.example.roombookingapp.data.Booking
import com.example.roombookingapp.data.BookingDao
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class BookRoomFragment : Fragment() {

    private lateinit var bookingDao: BookingDao
    private lateinit var session: UserSession
    private var selectedRoomId: Int = 0
    private var selectedRoomName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedRoomId = it.getInt("roomId")
            selectedRoomName = it.getString("roomName") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        bookingDao = db.bookingDao()
        session = UserSession(requireContext())

        val tvSelectedRoom = view.findViewById<TextView>(R.id.tvSelectedRoom)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val tvUnavailableSlots = view.findViewById<TextView>(R.id.tvUnavailableSlots)
        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etStartTime = view.findViewById<EditText>(R.id.etStartTime)
        val etEndTime = view.findViewById<EditText>(R.id.etEndTime)
        val etPeopleCount = view.findViewById<EditText>(R.id.etPeopleCount)
        
        if (selectedRoomName.isNotEmpty()) {
            tvSelectedRoom.text = "Selected Room: $selectedRoomName"
        }

        // Set default date to today
        val today = Calendar.getInstance()
        val initialDate = String.format(Locale.getDefault(), "%02d/%02d/%d", today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR))
        etDate.setText(initialDate)
        updateUnavailableSlots(initialDate, tvUnavailableSlots)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
            etDate.setText(selectedDate)
            updateUnavailableSlots(selectedDate, tvUnavailableSlots)
        }
        
        val cbProjector = view.findViewById<CheckBox>(R.id.cbProjector)
        val cbWhiteboard = view.findViewById<CheckBox>(R.id.cbWhiteboard)
        val cbConferencePhone = view.findViewById<CheckBox>(R.id.cbConferencePhone)
        val btnSave = view.findViewById<Button>(R.id.btnConfirmBooking)

        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        btnSave.setOnClickListener {
            val date = etDate.text.toString()
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            val peopleCount = etPeopleCount.text.toString().toIntOrNull() ?: 0
            
            val equipment = mutableListOf<String>()
            if (cbProjector.isChecked) equipment.add("Projector")
            if (cbWhiteboard.isChecked) equipment.add("Whiteboard")
            if (cbConferencePhone.isChecked) equipment.add("Conference Phone")

            if (date.isBlank() || startTime.isBlank() || endTime.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val conflicts = bookingDao.getConflictingBookings(selectedRoomId, date, startTime, endTime)
                if (conflicts.isNotEmpty()) {
                    Toast.makeText(requireContext(), "This time slot is already booked!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val booking = Booking(
                    userId = session.getUserId(),
                    roomId = selectedRoomId,
                    roomName = selectedRoomName,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    numberOfPeople = peopleCount,
                    equipmentList = equipment.joinToString(", ")
                )

                bookingDao.insertBooking(booking)
                Toast.makeText(requireContext(), "Booking Confirmed!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun updateUnavailableSlots(date: String, textView: TextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            val bookings = bookingDao.getAllBookings().filter { it.roomId == selectedRoomId && it.date == date && it.status == "CONFIRMED" }
            if (bookings.isEmpty()) {
                textView.text = "Unavailable Slots: None"
            } else {
                val slots = bookings.joinToString(", ") { "${it.startTime} - ${it.endTime}" }
                textView.text = "Unavailable Slots: $slots"
            }
        }
    }

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(requireContext(), { _, h, min ->
            val amPm = if (h < 12) "AM" else "PM"
            val h12 = if (h % 12 == 0) 12 else h % 12
            editText.setText(String.format(Locale.getDefault(), "%02d:%02d %s", h12, min, amPm))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
        timePickerDialog.show()
    }
}
