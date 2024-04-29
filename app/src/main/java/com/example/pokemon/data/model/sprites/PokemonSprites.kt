package com.example.pokemon.data.model.sprites

import com.google.gson.annotations.SerializedName

data class PokemonSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("official-artwork") val artwork: OfficialSprites?,
    @SerializedName("home") val home: HomeSprites?,
) {
    val bestFrontImageUrl: String?
        get() {
            return frontDefault ?: artwork?.frontDefault ?: home?.frontFemale ?: home?.frontDefault
        }

    fun hasPicture() = bestFrontImageUrl != null
}