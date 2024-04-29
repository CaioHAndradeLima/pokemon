package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.common.extension.toErrorMessage
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Flow<RequestResource<List<Pokemon>>> = flow {
        try {
            emit(RequestResource.Loading())
            val pokemons = repository.getPokemons()
                .filter { it.sprites?.hasPicture() == true }
                .map { it.copy() }
            emit(RequestResource.Success(pokemons))
        } catch (e: HttpException) {
            emit(RequestResource.Error(e.toErrorMessage()))
        } catch (e: IOException) {
            emit(RequestResource.Error(UiText.Resource(R.string.check_your_internet_connection)))
        }
    }
}