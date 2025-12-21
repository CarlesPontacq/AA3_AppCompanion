package com.example.appcompanion

import Models.PokemonCardAdapter
import PokemonApi.PokemonApiCall
import PokemonApi.PokemonCard
import PokemonApi.PokemonCardResponse
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardsFragment : Fragment() {

    //Card list
    private lateinit var recyclerView: RecyclerView
    private val cardsList = mutableListOf<PokemonCard>()
    private lateinit var adapter: PokemonCardAdapter
    private lateinit var loadingBar: ProgressBar

    //Retry connection
    private lateinit var errorLayout: View
    private lateinit var retryButton: Button

    //Category search
    private lateinit var pokemonButton: Button
    private lateinit var trainerButton: Button
    private lateinit var energyButton: Button

    private lateinit var searchView: SearchView
    private var lastQuery: String? = null
    private var queryCategory: String = "Pokémon"

    private var pokemonQuery: String = "Pokémon"
    private var trainerQuery: String = "Trainer"
    private var energyQuery: String = "Energy"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initialize layout
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        queryCategory = pokemonQuery

        recyclerView = view.findViewById(R.id.cardsRecycler)
        loadingBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)

        pokemonButton = view.findViewById(R.id.btn_pokemon)
        trainerButton = view.findViewById(R.id.btn_trainer)
        energyButton = view.findViewById(R.id.btn_energy)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = PokemonCardAdapter(cardsList)
        recyclerView.adapter = adapter

        errorLayout = view.findViewById(R.id.errorLayout)
        retryButton = view.findViewById(R.id.retryButton)

        //retry loading images + log a retry event
        retryButton.setOnClickListener {
            loadCards(lastQuery)
            AnalyticsManager.logRetryApiEvent()
        }

        //search by category buttons and change category to search
        pokemonButton.setOnClickListener{
            queryCategory = pokemonQuery
            updateCategoryButtons()
            loadCardsByCategory()
        }

        trainerButton.setOnClickListener{
            queryCategory = trainerQuery
            updateCategoryButtons()
            loadCardsByCategory()
        }

        energyButton.setOnClickListener{
            queryCategory = energyQuery
            updateCategoryButtons()
            loadCardsByCategory()
        }

        //Default load cards
        loadCards(null)

        //logic to check if the searchView is used (It's blank or not) and log a search event
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    loadCards(query)
                    AnalyticsManager.logSearchEvent(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    loadCards(null)
                }
                return true
            }
        })
        return view
    }

    //function to load cards by search or by default
    private fun loadCards(query: String? = null) {
        //First we show the loading progressBar and update the lastQuery
        showLoading()
        lastQuery = query

        Log.d("PokemonCard", "Loading: ${queryCategory} ${query}")

        //We make a random to select random cards by default and we search cards based of
        //if the query is null, blank or if it isn't
        val randomPage = (1..100).random()

        val call: Call<PokemonCardResponse> =
            if (query.isNullOrBlank()) {
                // Random
                PokemonApiCall.apiService.searchCards(
                    query = null,
                    page = randomPage,
                    pageSize = 21
                )
            } else {
                // Search
                PokemonApiCall.apiService.searchCards(
                    query = "name:$query",
                    page = 1,
                    pageSize = 21
                )
            }
        call.enqueue(object : Callback<PokemonCardResponse> {
            //If we connect to the api we add the cards found on the list and we show the content
            //If the api doesn't response we show the retry button
            override fun onResponse(
                call: Call<PokemonCardResponse>,
                response: Response<PokemonCardResponse>
            ) {
                if (response.isSuccessful) {
                    val cards = response.body()?.data ?: emptyList()

                    cardsList.clear()
                    cardsList.addAll(cards)
                    adapter.notifyDataSetChanged()

                    showContent()
                } else {
                    FirebaseCrashlytics.getInstance().recordException(
                        Exception("API error ${response.code()}")
                    )
                    showError()
                }
            }

            override fun onFailure(call: Call<PokemonCardResponse>, t: Throwable) {
                Log.e("PokemonCard", "Error: ${t.message}")

                FirebaseCrashlytics.getInstance().recordException(t)

                showError()
            }
        })
    }

    //function to load cards by category
    private fun loadCardsByCategory(query: String? = null) {
        //First we show the progressBar and we make a random to select random cards by default and category
        // and we call a log event to know if the user is searching by a category and which
        showLoading()
        lastQuery = query
        AnalyticsManager.logCategoryEvent(queryCategory)

        Log.d("PokemonCard", "Loading: ${queryCategory}")
        val randomPage = (1..100).random()

        val call: Call<PokemonCardResponse> =
            PokemonApiCall.apiService.searchCards(
                query = "supertypes:${queryCategory}",
                page = randomPage,
                pageSize = 21
            )

        call.enqueue(object : Callback<PokemonCardResponse> {
            //If we connect to the api we add the cards found on the list and we show the content
            //If the api doesn't response we show the retry button
            override fun onResponse(
                call: Call<PokemonCardResponse>,
                response: Response<PokemonCardResponse>
            ) {
                if (response.isSuccessful) {
                    val cards = response.body()?.data ?: emptyList()

                    cardsList.clear()
                    cardsList.addAll(cards)
                    adapter.notifyDataSetChanged()

                    showContent()
                } else {
                    FirebaseCrashlytics.getInstance().recordException(
                        Exception("API error ${response.code()}")
                    )
                    showError()
                }
            }

            override fun onFailure(call: Call<PokemonCardResponse>, t: Throwable) {
                Log.e("PokemonCard", "Error: ${t.message}")

                FirebaseCrashlytics.getInstance().recordException(t)

                showError()
            }
        })
    }

    //Functions to show the content, the progress bar or the error layout
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

    //function to know what category button is selected
    private fun updateCategoryButtons() {
        pokemonButton.isSelected = queryCategory == pokemonQuery
        trainerButton.isSelected = queryCategory == trainerQuery
        energyButton.isSelected = queryCategory == energyQuery
    }
}