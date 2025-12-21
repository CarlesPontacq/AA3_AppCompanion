package com.example.appcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Basic logic to manage the fragments
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.navbar)

        bottomNavigationView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }

        loadFragment(CardsFragment())
    }

    // function to load the selected fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
    }

    // function to detect when one fragment is selected and perform the loadFragment function
    private fun handleNavigationItemSelected(itemId: Int): Boolean {
        return when (itemId) {
            R.id.cards -> {
                loadFragment(CardsFragment())
                true
            }
            R.id.deck -> {
                loadFragment(DeckFragment())
                true
            }
            R.id.chat -> {
                loadFragment(ChatFragment())
                true
            }
            R.id.profile -> {
                loadFragment(ProfileFragment())
                true
            }
            else -> false
        }
    }
}