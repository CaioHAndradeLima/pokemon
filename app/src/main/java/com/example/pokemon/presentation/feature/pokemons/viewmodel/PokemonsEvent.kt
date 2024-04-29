package com.example.pokemon.presentation.feature.pokemons.viewmodel

sealed class PokemonsEvent {
    object StartRequest : PokemonsEvent()
}