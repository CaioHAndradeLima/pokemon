package com.example.pokemon.data.model

import com.example.pokemon.data.model.Pokemon

data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Pokemon>,
)