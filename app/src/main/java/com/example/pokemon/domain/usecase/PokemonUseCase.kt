package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PokemonUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(id: String): Flow<RequestResource<Pokemon>> = flow {
        emit(RequestResource.Loading())

        when (val pokemon = repository.getPokemon(id)) {
            is ResponseApi.Success -> emit(RequestResource.Success(pokemon.data))
            is ResponseApi.Error -> emit(RequestResource.Error(pokemon.message))
        }
    }.flowOn(Dispatchers.IO)
}