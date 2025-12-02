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
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //******* Start Google Auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("743427009820-9s0bc4joig8h05pt9f0qlev5g7tveu1c.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        account?.let{
            Log.d("Login Google", "Ya se ha robado la información de: " + account.displayName + " anteriormente")
        } ?: run {
            Log.d("Login Google", "No hay sesion iniciado")
            findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener{ signIn() }
        }

        findViewById<SignInButton>(R.id.btn_login_google).setOnClickListener{signIn()}
        //******* End Google Auth

        //******* Start User Auth
        emailField = findViewById(R.id.input_user)
        passwordField = findViewById(R.id.input_password)

        auth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.btn_register).setOnClickListener{Register()}
        findViewById<Button>(R.id.btn_login).setOnClickListener{Login()}
        //******* End User Auth



    }

    private fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 9001){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            if(task.isSuccessful){
                val account = task.getResult(ApiException::class.java)
                Log.d("Login Google", "Tengo la informacion de: " + account.displayName)
            }
            else{
                Log.d("Login Google", "Error " + task.exception)
            }
        }
    }

    private fun Register(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.createUserWithEmailAndPassword(email, password).
        addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Registro correcto", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun Login(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        auth.signInWithEmailAndPassword(email, password).
        addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Login correcto", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Error en el login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()

            }
        }
    }
}