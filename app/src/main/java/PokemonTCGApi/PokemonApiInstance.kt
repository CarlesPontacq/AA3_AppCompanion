package PokemonApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Retrofit interface that defines API endpoints
interface PokemonApiInstance {

    // Get a single card by its exact ID
    @GET("cards/{id}")
    fun getCard(
        @Path("id") cardId: String
    ): Call<SinglePokemonCardResponse>

    // Search cards using query parameters (name, supertype, etc.)
    @GET("cards")
    fun searchCards(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Call<PokemonCardResponse>
}