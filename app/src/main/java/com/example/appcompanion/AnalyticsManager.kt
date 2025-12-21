package com.example.appcompanion

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.http.Query

object AnalyticsManager : Application() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init(context: Context){
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    fun logRetryApiEvent(){
        firebaseAnalytics.logEvent("retry_card_search", null)
    }

    fun logSearchEvent(query: String){
        val bundle = Bundle().apply {
            putString("search_query", query)
        }
        firebaseAnalytics.logEvent("search_cards", bundle)
    }

    fun logCategoryEvent(query: String){
        val bundle = Bundle().apply {
            putString("category_card_search", query)
        }
        Log.d("PokemonCard", "Event ${query}")
        firebaseAnalytics.logEvent("category_card_search", bundle)    }
}