package Models

import com.example.appcompanion.R

enum class PokemonType(val drawableRes: Int) {
    COLORLESS(R.drawable.ic_colorless),
    DARKNESS(R.drawable.ic_darkness),
    DRAGON(R.drawable.ic_dragon),
    FAIRY(R.drawable.ic_fairy),
    FIGHTING(R.drawable.ic_fighting),
    FIRE(R.drawable.ic_fire),
    GRASS(R.drawable.ic_grass),
    LIGHTNING(R.drawable.ic_lightning),
    METAL(R.drawable.ic_metal),
    PSYCHIC(R.drawable.ic_psychic),
    WATER(R.drawable.ic_water);

    companion object {
        // Turns string received from API to enum
        fun fromString(type: String?): PokemonType {
            return when(type?.uppercase()) {
                "COLORLESS" -> COLORLESS
                "DARKNESS" -> DARKNESS
                "DRAGON" -> DRAGON
                "FAIRY" -> FAIRY
                "FIGHTING" -> FIGHTING
                "FIRE" -> FIRE
                "GRASS" -> GRASS
                "LIGHTNING" -> LIGHTNING
                "METAL" -> METAL
                "PSYCHIC" -> PSYCHIC
                "WATER" -> WATER
                else -> COLORLESS
            }
        }
    }
}
