package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import io.reactivex.rxjava3.core.Observable

class PokemonUseCase(
    private val repository: PokemonApiRepository
) {

    operator fun invoke(id: String): Observable<RequestResource<Pokemon>> {
        return Observable.concat(
            Observable.just(RequestResource.Loading()),
            repository.getPokemon(id)
                .map { response ->
                    when (response) {
                        is ResponseApi.Success -> RequestResource.Success(response.data)
                        is ResponseApi.Error -> RequestResource.Error(response.message)
                    }
                }
                .toObservable()
        )
    }
}
