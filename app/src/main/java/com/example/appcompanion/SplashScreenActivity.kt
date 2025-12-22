package com.example.appcompanion

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics

// Simple class that when the users clicks on any part of the screen it goes to the next screen
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var splashScreenLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenLayout = findViewById(R.id.splashLayout)
        splashScreenLayout.setOnClickListener{onButtonClick()}
    }

    private fun onButtonClick(){
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
    }
}