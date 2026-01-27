package com.example.appcompanion

import Models.LoginType
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager.OnActivityResultListener
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText

    private lateinit var prefs : SharedPreferences

    private val userPrefs : String = "user_prefs"
    private val isLoggedInPrefs : String = "is_logged_in"
    private val usernamePrefs : String = "username"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in, and if they are skip this activity
        prefs = getSharedPreferences(userPrefs, Context.MODE_PRIVATE)
        val isLoggedIn : Boolean = prefs.getBoolean(isLoggedInPrefs, false)

        if (isLoggedIn) {
            goToNextActivity()
            return
        }

        // If the user is not logged in already, continue with login/register screen
        setContentView(R.layout.activity_login)

        LoginManager.configure(this)

        findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener {
            LoginManager.loginType = LoginType.GOOGLE_LOGIN
            LoginManager.startSession(this)
        }

        emailField = findViewById(R.id.input_user)
        passwordField = findViewById(R.id.input_password)

        //logic that checks if the firebase login is correct or not, and if it is it loads the next activity
        // it also shows a success or error message, it is assigned to a button
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            LoginManager.loginType = LoginType.FIREBASE_LOGIN

            LoginManager.loginFirebaseEmail(email, password, {
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                saveLoginLocally(email)
                goToNextActivity()
            }, {
                error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            })

        }

        //logic that checks if the firebase register is correct or not, and if it is it loads the next activity
        // it also shows a success or error message, it is assigned to a button
        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            LoginManager.loginType = LoginType.FIREBASE_REGISTER

            LoginManager.registerFirebaseEmail(
                email,
                password,
                onSuccess = {
                    Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
                    saveLoginLocally(email)
                    goToNextActivity()
                },
                onError = { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    //function that checks if the google login is correct or not, and if it is it loads the next activity
    // it also shows a success or error message
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LoginManager.handleGoogleResult(
            requestCode,
            data,
            onSuccess = {
                val user = FirebaseAuth.getInstance().currentUser

                if(user != null){
                    val displayName = user.displayName ?: ""
                    saveLoginLocally(displayName)
                }

                Toast.makeText(this, "Login Google exitoso", Toast.LENGTH_SHORT).show()
                goToNextActivity()
            },
            onError = {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    //simple function to go to the next activity
    private fun goToNextActivity(){
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent)
    }

    private fun saveLoginLocally(user: String) {
        var username = ""
        if (user.contains("@"))
            username = user.substringBefore("@")
        else
            username = user
        prefs.edit()
            .putString(usernamePrefs, username)
            .putBoolean(isLoggedInPrefs, true)
            .apply()
    }
}