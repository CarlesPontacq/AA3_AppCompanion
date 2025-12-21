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

    private lateinit var recyclerView: RecyclerView
    private val cardsList = mutableListOf<PokemonCard>()
    private lateinit var adapter: PokemonCardAdapter
    private lateinit var loadingBar: ProgressBar

    private lateinit var errorLayout: View
    private lateinit var retryButton: Button

    private lateinit var searchView: SearchView
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        recyclerView = view.findViewById(R.id.cardsRecycler)
        loadingBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = PokemonCardAdapter(cardsList)
        recyclerView.adapter = adapter

        errorLayout = view.findViewById(R.id.errorLayout)
        retryButton = view.findViewById(R.id.retryButton)

        retryButton.setOnClickListener {
            loadCards(lastQuery)
            AnalyticsManager.logRetryApiEvent()
        }

        loadCards(null)

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

    private fun loadCards(query: String? = null) {
        showLoading()
        lastQuery = query

        Log.d("PokemonCard", "Loading: ${query}")
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
                // Búsqueda
                PokemonApiCall.apiService.searchCards(
                    query = "name:$query",
                    page = 1,
                    pageSize = 21
                )
            }

        call.enqueue(object : Callback<PokemonCardResponse> {

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