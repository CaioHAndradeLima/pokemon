package com.example.pokemon.data.repository

import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import io.reactivex.rxjava3.core.Single

interface PokemonApiRepository {

    fun getPokemons(): Single<ResponseApi<List<Pokemon>>>

    fun getPokemon(id: String): Single<ResponseApi<Pokemon>>
}