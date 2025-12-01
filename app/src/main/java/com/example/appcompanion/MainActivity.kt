package com.example.appcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var splashScreenLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreenLayout = findViewById(R.id.splashLayout)
        splashScreenLayout.setOnClickListener{onButtonClick()}
    }

    private fun onButtonClick(){
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
    }
}