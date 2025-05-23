package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository

class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    suspend operator fun invoke(): RequestResource<List<Pokemon>> =
        when (val pokemons = repository.getPokemons()) {
            is ResponseApi.Success -> {
                RequestResource.Success(
                    pokemons.data
                        .filter { it.sprites?.hasPicture() == true }
                        .map { it.copy() })
            }

            is ResponseApi.Error -> {
                RequestResource.Error(pokemons.message)
            }
        }
}