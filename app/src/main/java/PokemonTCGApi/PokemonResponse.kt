package PokemonApi

data class PokemonCardResponse(
    val data: List<PokemonCard>
)

data class PokemonCard(
    val id: String,
    val name: String,
    val supertype: String?,
    val subtypes: List<String>?,
    val types: List<String>?,
    val images: CardImages
)

data class CardImages(
    val small: String,
    val large: String
)