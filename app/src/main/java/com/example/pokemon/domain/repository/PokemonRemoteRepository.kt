package com.example.pokemon.domain.repository

import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApi
import com.example.pokemon.data.repository.PokemonApiRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PokemonRemoteRepository @Inject constructor(
    private val api: PokemonApi
) : PokemonApiRepository {

    @Throws(IOException::class, HttpException::class)
    override suspend fun getPokemons(): List<Pokemon> {
        val abilityResponse = api.getAbilityResponse(5, 5)
        return abilityResponse
            .results
            .map { api.getAbilityDetails(it.url) }
            .flatMap { it.pokemon }
            .mapNotNull { api.getPokemon(it.pokemon.url) }
    }

    @Throws(IOException::class, HttpException::class)
    override suspend fun getPokemon(id: String): Pokemon {
        return api.getPokemonById(id)
    }
}