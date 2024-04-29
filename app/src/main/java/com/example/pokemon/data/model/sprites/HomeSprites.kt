package com.example.pokemon.data.model.sprites

import com.google.gson.annotations.SerializedName

class HomeSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_female") val frontFemale: String?,
    @SerializedName("front_shiny") val frontShiny: String?,
)