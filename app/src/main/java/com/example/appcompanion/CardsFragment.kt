package com.example.appcompanion

import PokemonTCGApi.PokemonApiCall
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import java.math.BigInteger
import java.security.MessageDigest

class CardsFragment : Fragment() {
    private val privateKey = "68b5881e-be78-4765-8037-c4ca1e74af1f" //<- Key de Pokemon TCG API

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.cards_navigation)
    }

    private fun md5(input: String): String{
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}