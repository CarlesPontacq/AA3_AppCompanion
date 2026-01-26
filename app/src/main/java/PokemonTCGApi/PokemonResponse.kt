package PokemonApi

// API response wrapper containing a list of cards
data class PokemonCardResponse(
    val data: List<PokemonCard>
)

data class SinglePokemonCardResponse(
    val data: DetailedPokemonCard
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

// Data model representing a Pokemon card with more detail
data class DetailedPokemonCard(
    val id: String,
    val name: String,
    val supertype: String?,
    val subtypes: List<String>?,
    val types: List<String>?,
    val images: CardImages,
    val hp: String,
    val retreatCost: List<String>?,
    val attacks: List<CardAttack>?
)

// Card image URLs (small and large sizes)
data class CardImages(
    val small: String,
    val large: String
)

// Card attack info
data class CardAttack(
    val name: String,
    val cost: List<String>?,
    val damage: String,
    val text: String
)

