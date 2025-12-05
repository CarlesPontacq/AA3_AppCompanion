package PokemonTCGApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PokemonApiInstance {

    @GET("v2/cards") //<-- Enlace completo: "https://api.pokemontcg.io/v2/cards"
    fun getCards(
        @Query("apiKey") apiKey: String,
        @Query("ts") timeStamp: String,
        @Query("hash") hash: String,
        @Query("limit") limit: Int = 20
    ):Call<PokemonResponse>
}