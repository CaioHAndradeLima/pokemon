package com.example.pokemon.presentation.feature.pokemon.viewmodel

sealed class PokemonEvent {
    data class Find(
        internal val id: String
    ) : PokemonEvent()
}