package PokemonApi

// API response wrapper containing a list of cards
data class PokemonCardResponse(
    val data: List<PokemonCard>
)

// Data model representing a Pokemon card
data class PokemonCard(
    val id: String,
    val name: String,
    val supertype: String?,
    val subtypes: List<String>?,
    val types: List<String>?,
    val images: CardImages
)

// Card image URLs (small and large sizes)
data class CardImages(
    val small: String,
    val large: String
)