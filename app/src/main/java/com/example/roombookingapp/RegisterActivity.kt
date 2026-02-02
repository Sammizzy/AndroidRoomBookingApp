package com.example.roombookingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roombookingapp.data.AppDatabase
import com.example.roombookingapp.data.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        val userDao = AppDatabase.getDatabase(this).userDao()

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(this@RegisterActivity, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    userDao.registerUser(User(name = name, email = email, password = password))
                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }
}
