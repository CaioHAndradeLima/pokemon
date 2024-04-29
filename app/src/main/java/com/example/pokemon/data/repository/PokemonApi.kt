package com.example.pokemon.data.repository

import com.example.pokemon.data.model.Ability
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.model.PokemonAbilityResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface PokemonApi {

    @GET("ability")
    suspend fun getAbilityResponse(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonAbilityResponse

    @GET
    suspend fun getAbilityDetails(
        @Url url: String,
    ): Ability

    @GET
    suspend fun getPokemon(
        @Url url: String,
    ): Pokemon?

    @GET("pokemon/{id}")
    suspend fun getPokemonById(
        @Path("id") id: String,
    ): Pokemon
}