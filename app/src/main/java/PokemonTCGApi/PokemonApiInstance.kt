package PokemonApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiInstance {

    // Obtener una carta por ID
    @GET("cards/{id}")
    fun getCard(
        @Path("id") cardId: String
    ): Call<PokemonCardResponse>

    // Buscar cartas con query
    @GET("cards")
    fun searchCards(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Call<PokemonCardResponse>
}