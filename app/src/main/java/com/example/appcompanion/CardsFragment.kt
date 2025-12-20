package com.example.appcompanion

import Models.PokemonCardAdapter
import PokemonApi.PokemonApiCall
import PokemonApi.PokemonApiInstance
import PokemonApi.PokemonCard
import PokemonApi.PokemonCardResponse
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var recyclerView: RecyclerView
    private val cardsList = mutableListOf<PokemonCard>()
    private lateinit var adapter: PokemonCardAdapter
    private lateinit var loadingBar: ProgressBar

    private lateinit var errorLayout: View
    private lateinit var retryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        recyclerView = view.findViewById(R.id.cardsRecycler)
        loadingBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PokemonCardAdapter(cardsList)
        recyclerView.adapter = adapter

        errorLayout = view.findViewById(R.id.errorLayout)
        retryButton = view.findViewById(R.id.retryButton)

        retryButton.setOnClickListener {
            getRandomCards()
        }

        getRandomCards()
        return view
    }

    private fun getRandomCards(){
        showLoading()
        Log.d("PokemonCard", "Loading")
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
                        cardsList.clear()
                        cardsList.addAll(cards)
                        adapter.notifyDataSetChanged()
                        Log.d(
                            "PokemonCard", "Name: ${card.name}, Types: ${card.types}"
                        )
                    }

                    showContent()
                }
            }

            override fun onFailure(call: Call<PokemonCardResponse>, t: Throwable) {
                showError()

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

    private fun showLoading() {
        loadingBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    private fun showContent() {
        loadingBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun showError() {
        loadingBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }
}