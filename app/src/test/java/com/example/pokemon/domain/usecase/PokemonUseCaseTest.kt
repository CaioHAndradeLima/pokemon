package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class PokemonUseCaseTest {

    private val repository: PokemonApiRepository = mockk()
    private val pokemonUseCase = PokemonUseCase(repository)

    @Test
    fun `test PokemonUseCase emits loading and then success`() = runBlocking {
        // Given
        val fakePokemon = provideDefaultPokemonTest()
        val id = "1"
        coEvery { repository.getPokemon(id) } returns fakePokemon

        // When
        val result = mutableListOf<RequestResource<Pokemon>>()
        pokemonUseCase(id).collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Success)
        val successResult = result[1] as RequestResource.Success
        Assert.assertEquals(fakePokemon, successResult.data)
    }

    @Test
    fun `test PokemonUseCase emits error when HTTP exception occurs`() = runBlocking {
        // Given
        val id = "1"
        val response = mockk<retrofit2.Response<*>>(
            relaxed = true
        )
        val message = "error message"
        every { response.message() } returns message
        every { response.code() } returns 500

        val exception = HttpException(response);
        coEvery { repository.getPokemon(id) } throws exception
        // When
        val result = pokemonUseCase(id).toList()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Error)
        val errorResult = result[1] as RequestResource.Error
        Assert.assertEquals(UiText.Dynamic(exception.localizedMessage), errorResult.message)
    }

    @Test
    fun `test PokemonUseCase emits error when IO exception occurs`() = runBlocking {
        // Given
        val id = "1"
        coEvery { repository.getPokemon(id) } throws IOException()

        // When
        val result = mutableListOf<RequestResource<Pokemon>>()
        pokemonUseCase(id).collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Error)
        val errorResult = result[1] as RequestResource.Error
        Assert.assertEquals(UiText.Resource(R.string.check_your_internet_connection), errorResult.message)
    }
}
