package PokemonTCGApi

data class PokemonResponse(
    val code: Int,
    val status: String,
    val data: PokemonData
)

data class PokemonData(
    val results: List<PokemonCard>
)

data class PokemonCard(
    val id: Int,
    val name: String,
    val descrption: String,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val path: String,
    val extension: String
)