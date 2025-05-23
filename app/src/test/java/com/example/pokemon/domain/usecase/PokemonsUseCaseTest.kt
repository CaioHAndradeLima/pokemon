package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.assertInstanceOf
import com.example.pokemon.assertSameClass
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.data.repository.PokemonRemoteRepository
import com.example.pokemon.getHttpExceptionMessage
import com.example.pokemon.provider.provideDefaultPokemonTest
import com.example.pokemon.provider.providePokemonWithoutPictureTest
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
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonsUseCaseTest {

    private val repository = mockk<PokemonApiRepository>()
    private val pokemonsUseCase = PokemonsUseCase(repository)

    @Test
    fun `should return success when repository returns a valid pokemon list with pictures`() = runTest {
        // Given
        val pokemon = provideDefaultPokemonTest()
        val fakePokemons = listOf(pokemon)
        coEvery { repository.getPokemons() } returns ResponseApi.Success(fakePokemons)

        // When
        val result = pokemonsUseCase()

        // Then
        Assert.assertTrue(result is RequestResource.Success)
        val successResult = result as RequestResource.Success
        Assert.assertEquals(fakePokemons, successResult.data)
    }

    @Test
    fun `should return success with empty list when pokemons have no pictures`() = runTest {
        // Given
        val pokemonWithoutPicture = providePokemonWithoutPictureTest()
        val fakePokemons = listOf(pokemonWithoutPicture)
        coEvery { repository.getPokemons() } returns ResponseApi.Success(fakePokemons)

        // When
        val result = pokemonsUseCase()

        // Then
        Assert.assertTrue(result is RequestResource.Success)
        val successResult = result as RequestResource.Success
        Assert.assertEquals(emptyList<Pokemon>(), successResult.data)
    }

    @Test
    fun `should return error when HTTP error occurs`() = runTest {
        // Given
        val errorMessage = UiText.Dynamic("Server error")
        coEvery { repository.getPokemons() } returns ResponseApi.Error.Http(errorMessage)

        // When
        val result = pokemonsUseCase()

        // Then
        Assert.assertTrue(result is RequestResource.Error)
        val errorResult = result as RequestResource.Error
        Assert.assertEquals(errorMessage, errorResult.message)
    }

    @Test
    fun `should return error when Connection error occurs`() = runTest {
        // Given
        val expectedMessage = UiText.Resource(R.string.check_your_internet_connection)
        coEvery { repository.getPokemons() } returns ResponseApi.Error.Connection()

        // When
        val result = pokemonsUseCase()

        // Then
        Assert.assertTrue(result is RequestResource.Error)
        val errorResult = result as RequestResource.Error
        Assert.assertEquals(expectedMessage, errorResult.message)
    }
}
