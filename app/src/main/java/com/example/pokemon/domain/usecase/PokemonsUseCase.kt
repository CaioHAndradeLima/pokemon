package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Flow<RequestResource<List<Pokemon>>> = flow {
        emit(RequestResource.Loading())

        when (val pokemons = repository.getPokemons()) {
            is ResponseApi.Success -> {
                emit(
                    RequestResource.Success(
                    pokemons.data
                        .filter { it.sprites?.hasPicture() == true }
                        .map { it.copy() }
                ))
            }

            is ResponseApi.Error -> {
                emit(RequestResource.Error(pokemons.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}