package com.example.appcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbarView: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find navbar and set listener to change fragment when tapped
        bottomNavigationView = findViewById(R.id.navbar)
        bottomNavigationView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }

        // Find custom toolbar and set it as action bar
        toolbarView = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbarView)

        // Open fragment for Cards, the default screen
        loadFragment(CardsFragment())
    }

    // Replace current fragment with the specified one
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
    }

    // Handle navbar item being tapped (change current screen)
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