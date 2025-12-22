package com.example.appcompanion

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

//Singleton to manage the login information
object LoginManager {
    private const val requestCodeGoogle = 9001

    var loginType: Int? = null

    private lateinit var googleClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()

    // function to set up the google configurations for the login
    fun configure(activity: Activity){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("743427009820-9s0bc4joig8h05pt9f0qlev5g7tveu1c.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(activity, gso)
    }

    // function that manages what the user is doing
    // (login/register with google, login with firebase or register with firebase)
    fun startSession(activity: Activity){
        when(loginType){
            0 -> loginGoogle(activity)
            1 -> Log.d("LoginManager", "Intentando hacer Login de Firebase")
            2 -> Log.d("LoginManager", "Intentando hacer Register de Firebase")
            else -> Log.e("LoginManager", "Tipo de login inválido")
        }
    }

    //Actual function to login with google
    private fun loginGoogle(activity: Activity) {
        val intent = googleClient.signInIntent
        activity.startActivityForResult(intent, requestCodeGoogle)
    }

    fun handleGoogleResult(requestCode: Int, data: Intent?,
                           onSuccess: () -> Unit,
                           onError: (String) -> Unit) {
        if (requestCode != requestCodeGoogle) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    onSuccess()
                } else {
                    onError(result.exception?.message ?: "Error desconocido")
                }
            }

        } catch (e: Exception) {
            onError(e.message ?: "Error obteniendo cuenta")
        }
    }

    // function to login with firebase and checks if it is successful or not
    fun loginFirebaseEmail(email: String, password: String,
                           onSuccess: () -> Unit,
                           onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception?.message ?: "Error desconocido")
            }
    }

    // function to register with firebase and checks if it is successful or not
    fun registerFirebaseEmail(email: String, password: String,
                              onSuccess: () -> Unit,
                              onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception?.message ?: "Error desconocido")
            }
    }
}