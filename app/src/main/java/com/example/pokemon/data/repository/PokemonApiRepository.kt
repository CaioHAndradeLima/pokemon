package com.example.pokemon.data.repository

import com.example.pokemon.data.model.Pokemon

interface PokemonApiRepository {

    suspend fun getPokemons(): List<Pokemon>

    suspend fun getPokemon(id: String): Pokemon
}