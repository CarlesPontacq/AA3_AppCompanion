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

object LoginManager {
    private const val requestCodeGoogle = 9001

    var loginType: Int? = null

    private lateinit var googleClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()

    fun configure(activity: Activity){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("743427009820-9s0bc4joig8h05pt9f0qlev5g7tveu1c.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(activity, gso)
    }

    fun startSession(activity: Activity){
        when(loginType){
            0 -> loginGoogle(activity)
            1 -> Log.d("LoginManager", "Listo para Firebase Email/Password")
            else -> Log.e("LoginManager", "Tipo de login inválido")
        }
    }

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

    fun loginFirebaseEmail(email: String, password: String,
                           onSuccess: () -> Unit,
                           onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onError(task.exception?.message ?: "Error desconocido")
            }
    }

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