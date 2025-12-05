package com.example.appcompanion

import PokemonTCGApi.PokemonApiCall
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigInteger
import java.security.MessageDigest
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardListDisplayActivity : AppCompatActivity() {
    private val privateKey = "68b5881e-be78-4765-8037-c4ca1e74af1f" //<- Key de Pokemon TCG API


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list_display)

        val timestamp = System.currentTimeMillis().toString()
        val hash = md5("$timestamp$privateKey")

        val call = PokemonApiCall.apiService.getCards(privateKey, timestamp, hash)

        /*
        call.enqueue(object : Callback<MarvelResponse> {
            override fun onResponse(call: Call<MarvelResponse>, response: Response<MarvelResponse>) {
                if (response.isSuccessful) {
                    val characters = response.body()?.data?.results
                    characters?.forEach { character ->
                        Log.d("Character", "Name: ${character.name}, Description: ${character.descrption}")
                    }
                }else {
                    Log.e("ApiError", "Response not successful: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<MarvelResponse>, t: Throwable) {
                Log.e("ApiError", t.message ?: "Unknown error")
            }
        })
         */
    }

        private fun md5(input: String): String{
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}