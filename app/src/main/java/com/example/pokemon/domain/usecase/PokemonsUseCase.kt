package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PokemonsUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(): Observable<RequestResource<List<Pokemon>>> {
        return Observable.concat(
            Observable.just(RequestResource.Loading()),
            repository.getPokemons()
                .map { response ->
                    when (response) {
                        is ResponseApi.Success -> RequestResource.Success(
                            response.data
                                .filter { it.sprites?.hasPicture() == true }
                                .map { it.copy() }
                        )
                        is ResponseApi.Error -> RequestResource.Error(response.message)
                    }
                }
                .toObservable()
        )
    }
}
