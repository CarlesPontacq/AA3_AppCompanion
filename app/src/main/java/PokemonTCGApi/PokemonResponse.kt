package PokemonApi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// API response wrapper containing a list of cards
data class PokemonCardResponse(
    val data: List<PokemonCard>
)

// Data model representing a Pokemon card
@Parcelize
data class PokemonCard(
    val id: String,
    val name: String,
    val supertype: String?,
    val subtypes: List<String>?,
    val types: List<String>?,
    val images: CardImages
) : Parcelable

// Data model representing a Pokemon card with more detail
@Parcelize
data class DetailedPokemonCard(
    val id: String,
    val name: String,
    val supertype: String?,
    val subtypes: List<String>?,
    val types: List<String>?,
    val images: CardImages,
    val hp: Int,
    val retreatCost: List<String>?,
    val attacks: List<CardAttack>?
) : Parcelable

// Card image URLs (small and large sizes)
@Parcelize
data class CardImages(
    val small: String,
    val large: String
) : Parcelable

// Card attack info
@Parcelize
data class CardAttack(
    val name: String,
    val cost: List<String>?,
    val damage: Int,
    val text: String
) : Parcelable

