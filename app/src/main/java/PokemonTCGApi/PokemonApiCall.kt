package PokemonApi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton responsible for configuring Retrofit and API access
object PokemonApiCall {

    // Pokemon TCG API key and Base URL for the Pokemon TCG API
    private const val API_KEY = "68b5881e-be78-4765-8037-c4ca1e74af1f" //<- Key de Pokemon TCG API
    private const val BASE_URL =  "https://api.pokemontcg.io/v2/"

    // Interceptor to add the API key to every request
    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", API_KEY)
            .build()
        chain.proceed(request)
    }

    // OkHttp client configuration with timeouts and interceptor
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    // Retrofit API service instance
    val apiService: PokemonApiInstance by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(PokemonApiInstance::class.java)
    }

}