package com.example.pokemon.data.repository

import com.example.pokemon.data.model.Ability
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.model.PokemonAbilityResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface PokemonApi {

    @GET("ability")
    fun getAbilityResponse(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Single<PokemonAbilityResponse>

    @GET
    fun getAbilityDetails(
        @Url url: String,
    ): Single<Ability>

    @GET
    fun getPokemon(
        @Url url: String,
    ): Single<Pokemon>

    @GET("pokemon/{id}")
    fun getPokemonById(
        @Path("id") id: String,
    ): Single<Pokemon>
}