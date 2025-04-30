package com.example.pokemon.data.repository

import com.example.pokemon.R
import com.example.pokemon.common.extension.toErrorMessage
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    override suspend fun getPokemons() = try {
        val abilityResponse = api.getAbilityResponse(5, 5)
        ResponseApi.Success(
            abilityResponse
                .results
                .map { api.getAbilityDetails(it.url) }
                .flatMap { it.pokemon }
                .mapNotNull { api.getPokemon(it.pokemon.url) }
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }

    override suspend fun getPokemon(id: String) = try {
        ResponseApi.Success(
            api.getPokemonById(id)
        )
    } catch (e: HttpException) {
        ResponseApi.Error.Http(e.toErrorMessage())
    } catch (e: IOException) {
        ResponseApi.Error.Connection(UiText.Resource(R.string.check_your_internet_connection))
    }
}