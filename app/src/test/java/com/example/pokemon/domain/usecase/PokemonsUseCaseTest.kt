package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.assertInstanceOf
import com.example.pokemon.assertSameClass
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.getHttpExceptionMessage
import com.example.pokemon.provider.provideDefaultPokemonTest
import com.example.pokemon.provider.providePokemonWithoutPictureTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PokemonsUseCaseTest {

    private val repository: PokemonApiRepository = mockk()
    private val pokemonsUseCase = PokemonsUseCase(repository)

    @Test
    fun `test PokemonsUseCase emits loading and then success`() = runBlocking {
        // Given
        val pokemon = provideDefaultPokemonTest()
        val fakePokemons = listOf(pokemon)
        coEvery { repository.getPokemons() } returns fakePokemons

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
        coEvery { repository.getPokemons() } returns fakePokemons

        // When
        val result = mutableListOf<RequestResource<List<Pokemon>>>()
        pokemonsUseCase().collect { result.add(it) }

        // Then
        Assert.assertTrue(result[0] is RequestResource.Loading)
        Assert.assertTrue(result[1] is RequestResource.Success)
        val successResult = result[1] as RequestResource.Success
        Assert.assertEquals(listOf<Pokemon>(), successResult.data)
    }

    @Test
    fun `test PokemonsUseCase emits error when HTTP exception occurs`() = runBlocking {
        // Given
        val response = mockk<Response<*>>(
            relaxed = true
        )
        val message = "error message"
        every { response.message() } returns message
        every { response.code() } returns 500

        coEvery { repository.getPokemons() } throws HttpException(response)
        val uiTextExpected = getHttpExceptionMessage(message)
        // When
        val result = pokemonsUseCase().toList()

        // Then
        Assert.assertEquals(2, result.size)
        assertSameClass(RequestResource.Loading<List<Pokemon>>(), result[0])
        assertSameClass(RequestResource.Error<List<Pokemon>>(message = uiTextExpected), result[1])
        assertSameClass(uiTextExpected, result[1].message)
        assertInstanceOf<UiText.Dynamic>(result[1].message)
    }

    @Test
    fun `test PokemonsUseCase emits error when IO exception occurs`() = runBlocking {
        // Given
        coEvery { repository.getPokemons() } throws IOException()

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
