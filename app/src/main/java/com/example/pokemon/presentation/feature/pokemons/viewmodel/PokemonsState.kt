package com.example.pokemon.presentation.feature.pokemons.viewmodel

import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon

sealed class PokemonsState {
    data class Show(
        val pokemons: List<Pokemon>
    ) : PokemonsState()

    object Loading: PokemonsState()
    data class TryAgain(val errorMessage: UiText): PokemonsState()
}

