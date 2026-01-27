package com.example.appcompanion

import Models.PokemonCardAdapter
import PokemonApi.PokemonApiCall
import PokemonApi.PokemonCard
import PokemonApi.PokemonCardResponse
import android.content.Context
import android.content.SharedPreferences
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
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appcompanion.AnalyticsManager.getSharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CardsFragment : Fragment() {

    //Card list
    private lateinit var swipeRefresh: SwipeRefreshLayout
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

    private lateinit var prefs : SharedPreferences

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

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setProgressViewOffset(false, 275, 375)

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

        prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        swipeRefresh.setOnRefreshListener {
            resetLocalCards()
            loadCards(null)
            swipeRefresh.isRefreshing = false
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

    private fun loadCardsFromCache(): List<PokemonCard>? {
        val jsonOfCards = prefs.getString("cached_cards", null) ?: return null
        val type = object : TypeToken<List<PokemonCard>>() {}.type
        return Gson().fromJson(jsonOfCards, type)
    }

    //function to load cards by search or by default
    private fun loadCards(query: String? = null) {
        //First we show the loading progressBar and update the lastQuery
        showLoading()
        lastQuery = query

        // Check if default cards result is already stored, and if so load that instead of calling the API
        val cached = loadCardsFromCache()
        if (cached != null && query.isNullOrBlank()) {
            cardsList.clear()
            cardsList.addAll(cached)
            adapter.notifyDataSetChanged()
            showContent()
            return
        }

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

                    saveCardsLocally(cards)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.cards_navigation)
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

    // Save current cards to avoid having to call the API again once it already has loaded
    private fun saveCardsLocally(cards: List<PokemonCard>) {
        val jsonOfCards = Gson().toJson(cards)
        prefs.edit().putString("cached_cards", jsonOfCards).apply()
    }

    // Reset cards stored locally so that the API knows to call for new ones
    private fun resetLocalCards() {
        prefs.edit().putString("cached_cards", null).apply()
    }
}