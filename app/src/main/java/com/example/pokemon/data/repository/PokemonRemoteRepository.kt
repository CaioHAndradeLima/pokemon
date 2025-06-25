package com.example.pokemon.data.repository

import com.example.pokemon.R
import com.example.pokemon.common.extension.toErrorMessage
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override fun getPokemons(): Single<ResponseApi<List<Pokemon>>> {
        return Single.fromCallable {
            try {
                val abilityResponse = api.getAbilityResponse(5, 5).blockingGet()
                val abilityDetails = abilityResponse.results
                    .map { api.getAbilityDetails(it.url).blockingGet() }
                val pokemons = abilityDetails
                    .flatMap { it.pokemon }
                    .mapNotNull { api.getPokemon(it.pokemon.url).blockingGet() }

                ResponseApi.Success(pokemons)
            } catch (e: HttpException) {
                ResponseApi.Error.Http(e.toErrorMessage())
            } catch (e: IOException) {
                ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
            } catch (e: Exception) {
                ResponseApi.Error.Unknown(e, UiText.Dynamic(e.message ?: "Unknown error"))
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun getPokemon(id: String): Single<ResponseApi<Pokemon>> {
        return Single.fromCallable {
            try {
                val pokemon = api.getPokemonById(id).blockingGet()
                ResponseApi.Success(pokemon)
            } catch (e: HttpException) {
                ResponseApi.Error.Http(e.toErrorMessage())
            } catch (e: IOException) {
                ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
            } catch (e: Exception) {
                ResponseApi.Error.Unknown(e, UiText.Dynamic(e.message ?: "Unknown error"))
            }
        }.subscribeOn(Schedulers.io())
    }
}