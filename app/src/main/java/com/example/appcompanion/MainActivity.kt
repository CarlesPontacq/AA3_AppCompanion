package com.example.appcompanion

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbarView: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        //Basic logic to manage the fragments
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

        // Create and set top left menu icon
        toolbarView.setNavigationIcon(R.drawable.nav_menu_white_48dp)
        toolbarView.setNavigationOnClickListener {

        }

        // Open fragment for Cards, the default screen
        loadFragment(CardsFragment())
    }

    // Replace current fragment with the specified one
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()

        // Change toolbar options based on current fragment
        showToolbarMenuButton()
        /*
        when(fragment) {
            is CardsFragment -> showToolbarMenuButton()
            else -> showBackArrowButton()
        }*/
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

    // Inflate custom toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_toolbar, menu)
        return true
    }

    // Show and manage menu icon from top left of the toolbar
    private fun showToolbarMenuButton()
    {
        toolbarView.navigationIcon = ContextCompat.getDrawable(this, R.drawable.nav_menu_white_48dp)
        toolbarView.setNavigationOnClickListener {
            showLeftMenuPopup()
        }
    }

    // Show and manage back arrow from top left of the toolbar
    private fun showBackArrowButton()
    {
        toolbarView.navigationIcon = ContextCompat.getDrawable(this, R.drawable.nav_chat_white_24dp)
        toolbarView.setNavigationOnClickListener {

        }
    }

    // Show and manage popup menu from top left menu icon in the toolbar
    private fun showLeftMenuPopup() {
        val popup = PopupMenu(this, toolbarView)
        popup.menuInflater.inflate(R.menu.left_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.changeTheme -> {
                    changeAppTheme()
                    true
                }
                R.id.closeApp -> {
                    closeApp()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    // Change app theme between light and dark
    private fun changeAppTheme()
    {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    // Close application
    private fun closeApp()
    {
        finishAffinity()
    }
}