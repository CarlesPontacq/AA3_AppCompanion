package com.example.appcompanion

import PokemonApi.PokemonApiCall
import PokemonApi.PokemonApiInstance
import PokemonApi.PokemonCardResponse
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

class CardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //apiConnection();
        getRandomCards()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    private fun getRandomCards(){
        val randomPage = (1..100).random()

        val call = PokemonApiCall.apiService.searchCards(
            query = null,
            page = randomPage,
            pageSize = 20
        )

        call.enqueue(object : Callback<PokemonCardResponse> {

            override fun onResponse(
                call: Call<PokemonCardResponse>,
                response: Response<PokemonCardResponse>
            ) {
                if (response.isSuccessful) {
                    val cards = response.body()?.data ?: emptyList()
                    cards.forEach{ card ->
                        Log.d(
                            "PokemonCard", "Name: ${card.name}, Types: ${card.types}"
                        )
                        // Aquí actualizas el RecyclerView
                    }
                }
            }

            override fun onFailure(call: Call<PokemonCardResponse>, t: Throwable) {
                Log.e("PokemonCard", "Error: ${t.message}")
            }
        })
    }
    private fun apiConnection(){
        val call = PokemonApiCall.apiService.searchCards("name: pickachu")

        call.enqueue(object : Callback<PokemonCardResponse>{
            override fun onResponse(
                call: Call<PokemonCardResponse>,
                response: Response<PokemonCardResponse>
            ) {
                if(response.isSuccessful){
                    val cards = response.body()?.data ?: emptyList()

                    cards.forEach{ card ->
                        Log.d(
                            "PokemonCard", "Name: ${card.name}, Types: ${card.types}"
                        )
                    }
                }
                else{
                    Log.e(
                        "PokemonCard",
                        "Response error: ${response.code()} - ${response.message()}"
                    )
                }
            }
            override fun onFailure(call: Call<PokemonCardResponse>, t: Throwable) {
                Log.e("ApiError", t.message ?: "Unknown error")
            }
        })
    }
}