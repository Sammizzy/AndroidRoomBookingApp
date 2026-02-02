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

        val db = AppDatabase.getDatabase(requireContext())
        val userDao = db.userDao()

        lifecycleScope.launch {
            val user = userDao.getUserById(session.getUserId())
            user?.let {
                tvName.text = "Name: ${it.name}"
                tvEmail.text = "Email: ${it.email}"
            }
        }

        btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}
