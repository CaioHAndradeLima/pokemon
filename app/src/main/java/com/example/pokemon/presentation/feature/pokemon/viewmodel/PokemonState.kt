package com.example.pokemon.presentation.feature.pokemon.viewmodel

import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon

sealed class PokemonState {
    data class Show(
        val pokemon: Pokemon
    ) : PokemonState()

    object Loading : PokemonState()
    data class TryAgain(val errorMessage: UiText) : PokemonState()
}

