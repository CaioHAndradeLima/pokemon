package com.example.pokemon.data.model

import com.example.pokemon.data.model.sprites.PokemonSprites
import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int,
    val name: String,
    @SerializedName("base_experience") val baseExperience: Int,
    val height: Int?,
    val isDefault: Boolean?,
    val order: Int?,
    val weight: Int?,
    val sprites: PokemonSprites?,
    val species: PokemonSpecies?,
    val moves: List<PokemonMoves>?,
)