package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import com.example.pokemon.provider.providePokemonWithoutPictureTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class PokemonsUseCaseTest {

    private val repository = mockk<PokemonApiRepository>()
    private val pokemonsUseCase = PokemonsUseCase(repository)

    @Test
    fun `test PokemonsUseCase emits loading and then success`() = runBlocking {
        // Given
        val pokemon = provideDefaultPokemonTest()
        val fakePokemons = listOf(pokemon)
        val responseApi = ResponseApi.Success(fakePokemons)
        coEvery { repository.getPokemons() } returns responseApi

        // When
        val result = mutableListOf<RequestResource<List<Pokemon>>>()
        pokemonsUseCase().collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Success)
        val successResult = result[1] as RequestResource.Success
        Assert.assertEquals(fakePokemons, successResult.data)
    }

    @Test
    fun `test PokemonsUseCase no sprites should emits loading and then success without information`() = runBlocking {
        // Given
        val pokemon = providePokemonWithoutPictureTest()
        val fakePokemons = listOf(pokemon)
        coEvery { repository.getPokemons() } returns ResponseApi.Success(fakePokemons)

        // When
        val result = mutableListOf<RequestResource<List<Pokemon>>>()
        pokemonsUseCase().collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Success)
        val successResult = result[1] as RequestResource.Success
        Assert.assertEquals(emptyList<Pokemon>(), successResult.data)
    }

    @Test
    fun `test PokemonsUseCase emits error when HTTP error occurs`() = runBlocking {
        // Given
        val message = "error message"
        val uiTextExpected = UiText.Dynamic(message)
        coEvery { repository.getPokemons() } returns ResponseApi.Error.Http(uiTextExpected)

        // When
        val result = pokemonsUseCase().toList()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Error)
        val errorResult = result[1] as RequestResource.Error
        Assert.assertEquals(uiTextExpected, errorResult.message)
        Assert.assertTrue(errorResult.message is UiText.Dynamic)
    }

    @Test
    fun `test PokemonsUseCase emits error when IO error occurs`() = runBlocking {
        // Given
        coEvery { repository.getPokemons() } returns ResponseApi.Error.Connection()

        // When
        val result = mutableListOf<RequestResource<List<Pokemon>>>()
        pokemonsUseCase().collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Error)
        val errorResult = result[1] as RequestResource.Error
        Assert.assertEquals(UiText.Resource(R.string.check_your_internet_connection), errorResult.message)
    }
}
