package com.example.pokemon.di

import com.example.pokemon.data.repository.PokemonApi
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.domain.repository.PokemonRemoteRepository
import com.example.pokemon.domain.usecase.PokemonUseCase
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.presentation.feature.pokemon.viewmodel.PokemonViewModel
import com.example.pokemon.presentation.feature.pokemons.viewmodel.PokemonsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): PokemonApi {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonRepository(api: PokemonApi): PokemonApiRepository {
        return PokemonRemoteRepository(api)
    }

    //use case
    @Provides
    @Singleton
    fun providePokemonsUseCase(repository: PokemonApiRepository) = PokemonsUseCase(
        repository = repository,
    )
    @Provides
    @Singleton
    fun providePokemonUseCase(repository: PokemonApiRepository) = PokemonUseCase(
        repository = repository,
    )

    //viewmodel
    @Provides
    fun providePokemonsViewModel(case: PokemonsUseCase) = PokemonsViewModel(case)
    @Provides
    fun providePokemonViewModel(case: PokemonUseCase) = PokemonViewModel(case)
}