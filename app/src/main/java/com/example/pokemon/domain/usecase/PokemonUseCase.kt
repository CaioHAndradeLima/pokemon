package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository

class PokemonUseCase(
    private val repository: PokemonApiRepository
) {

    suspend operator fun invoke(id: String): RequestResource<Pokemon> =
        when (val pokemon = repository.getPokemon(id)) {
            is ResponseApi.Success -> {
                RequestResource.Success(pokemon.data)
            }

            is ResponseApi.Error -> {
                RequestResource.Error(pokemon.message)
            }
        }
}