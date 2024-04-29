package com.example.pokemon.data.model

data class PokemonAbilityResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AbilityInfoPokemon>,
)