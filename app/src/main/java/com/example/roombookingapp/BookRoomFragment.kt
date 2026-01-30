package com.example.roombookingapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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

    private lateinit var dao: BookingDao

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
        dao = db.bookingDao()

        val etRoom = view.findViewById<EditText>(R.id.etRoom)
        val etDate = view.findViewById<EditText>(R.id.etDate)
        val etTime = view.findViewById<EditText>(R.id.etTime)
        val etPeopleCount = view.findViewById<EditText>(R.id.etPeopleCount)
        val cbProjector = view.findViewById<CheckBox>(R.id.cbProjector)
        val cbWhiteboard = view.findViewById<CheckBox>(R.id.cbWhiteboard)
        val cbConferencePhone = view.findViewById<CheckBox>(R.id.cbConferencePhone)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Set up Date Picker
        etDate.isFocusable = false
        etDate.isClickable = true
        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        // Set up Time Picker
        etTime.isFocusable = false
        etTime.isClickable = true
        etTime.setOnClickListener {
            showTimePicker(etTime)
        }

        btnSave.setOnClickListener {
            val roomName = etRoom.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val peopleCount = etPeopleCount.text.toString().toIntOrNull() ?: 0
            
            val equipment = mutableListOf<String>()
            if (cbProjector.isChecked) equipment.add("Projector")
            if (cbWhiteboard.isChecked) equipment.add("Whiteboard")
            if (cbConferencePhone.isChecked) equipment.add("Conference Phone")

            if (roomName.isBlank() || date.isBlank() || time.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val booking = Booking(
                roomId = 0,
                roomName = roomName,
                date = date,
                time = time,
                numberOfPeople = peopleCount,
                equipmentList = equipment.joinToString(", ")
            )

            viewLifecycleOwner.lifecycleScope.launch {
                dao.insertBooking(booking)
                Toast.makeText(requireContext(), "Booking Saved!", Toast.LENGTH_SHORT).show()
                // Clear fields
                etRoom.text.clear()
                etDate.text.clear()
                etTime.text.clear()
                etPeopleCount.text.clear()
                cbProjector.isChecked = false
                cbWhiteboard.isChecked = false
                cbConferencePhone.isChecked = false
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                editText.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour < 12) "AM" else "PM"
                val hour12 = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, selectedMinute, amPm)
                editText.setText(formattedTime)
            },
            hour,
            minute,
            false // Use 12-hour format with AM/PM
        )
        timePickerDialog.show()
    }
}
