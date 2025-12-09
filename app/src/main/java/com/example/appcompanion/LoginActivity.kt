package com.example.appcompanion

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        LoginManager.configure(this)

        findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener {
            LoginManager.loginType = 0
            LoginManager.startSession(this)
        }

        emailField = findViewById(R.id.input_user)
        passwordField = findViewById(R.id.input_password)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            LoginManager.loginType = 1

            LoginManager.loginFirebaseEmail(email, password, {
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
                goToNextActivity()
            }, {
                error -> Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            })

        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            LoginManager.loginType = 2

            LoginManager.registerFirebaseEmail(
                email,
                password,
                onSuccess = {
                    Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
                    goToNextActivity()
                },
                onError = { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        LoginManager.handleGoogleResult(
            requestCode,
            data,
            onSuccess = {
                Toast.makeText(this, "Login Google exitoso", Toast.LENGTH_SHORT).show()
                goToNextActivity()
            },
            onError = {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun goToNextActivity(){
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent)
    }
}