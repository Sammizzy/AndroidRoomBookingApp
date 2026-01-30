package com.example.roombookingapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnBook).setOnClickListener {
            loadFragment(BookRoomFragment())
        }

        findViewById<Button>(R.id.btnView).setOnClickListener {
            loadFragment(ViewBookingsFragment())
        }

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(BookRoomFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
