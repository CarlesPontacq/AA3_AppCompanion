package com.example.appcompanion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout

class ProfileFragment : Fragment() {
    private lateinit var logOutButton : Button

    private lateinit var prefs : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.profile_navigation)

        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        logOutButton = view.findViewById(R.id.logoutButton)
        logOutButton.setOnClickListener{
            logOut()
        }
    }

    // Clear user preferences and return to login activity
    private fun logOut()
    {
        prefs.edit().clear().apply()

        val intent = Intent(requireContext(), LoginActivity::class.java);
        startActivity(intent)
    }
}