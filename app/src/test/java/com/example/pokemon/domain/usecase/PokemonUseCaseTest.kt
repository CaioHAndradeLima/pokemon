package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonUseCaseTest {

    private val repository: PokemonApiRepository = mockk()
    private val pokemonUseCase = PokemonUseCase(repository)

    @Test
    fun `should return success when repository returns a pokemon`() = runTest {
        // Given
        val fakePokemon = provideDefaultPokemonTest()
        val id = "1"
        coEvery { repository.getPokemon(id) } returns ResponseApi.Success(fakePokemon)

        // When
        val result = pokemonUseCase(id)

        // Then
        Assert.assertTrue(result is RequestResource.Success)
        val successResult = result as RequestResource.Success
        Assert.assertEquals(fakePokemon, successResult.data)
    }

    @Test
    fun `should return error when repository returns Http error`() = runTest {
        // Given
        val id = "1"
        val errorMessage = UiText.Dynamic("Something went wrong")
        coEvery { repository.getPokemon(id) } returns ResponseApi.Error.Http(errorMessage)

        // When
        val result = pokemonUseCase(id)

        // Then
        Assert.assertTrue(result is RequestResource.Error)
        val errorResult = result as RequestResource.Error
        Assert.assertEquals(errorMessage, errorResult.message)
    }

    @Test
    fun `should return error when repository returns Connection error`() = runTest {
        // Given
        val id = "1"
        val expectedMessage = UiText.Resource(R.string.check_your_internet_connection)
        coEvery { repository.getPokemon(id) } returns ResponseApi.Error.Connection()

        // When
        val result = pokemonUseCase(id)

        // Then
        Assert.assertTrue(result is RequestResource.Error)
        val errorResult = result as RequestResource.Error
        Assert.assertEquals(expectedMessage, errorResult.message)
    }
}
