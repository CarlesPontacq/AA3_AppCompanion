package com.example.appcompanion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.navbar)

        bottomNavigationView.setOnItemSelectedListener { item ->
            handleNavigationItemSelected(item.itemId)
        }
    }

    private fun handleNavigationItemSelected(itemId: Int): Boolean {
        return when (itemId) {
            R.id.cards -> {

                true
            }
            R.id.deck -> {

                true
            }
            R.id.chat -> {

                true
            }
            R.id.profile -> {

                true
            }
            else -> false
        }
    }
}