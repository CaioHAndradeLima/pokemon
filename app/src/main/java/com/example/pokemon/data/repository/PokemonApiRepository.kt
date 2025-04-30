package com.example.pokemon.data.repository

import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon

interface PokemonApiRepository {

    suspend fun getPokemons(): ResponseApi<List<Pokemon>>

    suspend fun getPokemon(id: String): ResponseApi<Pokemon>
}