package com.example.pokemon.domain.repository

import com.example.pokemon.data.model.Ability
import com.example.pokemon.data.model.AbilityInfoPokemon
import com.example.pokemon.data.model.AbilityPokemonDetail
import com.example.pokemon.data.model.PokemonAbilityResponse
import com.example.pokemon.data.repository.PokemonApi
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class PokemonRemoteRepositoryTest {

    private val api: PokemonApi = mockk()
    private val repository = PokemonRemoteRepository(api)

    @Test
    fun `test getPokemons success`() {
        runBlocking {
            // Given
            val abilityResponse = PokemonAbilityResponse(
                results = listOf(
                    AbilityInfoPokemon(
                        name = "name1",
                        url = "url1",
                    ),
                ),
                count = 1,
                next = "next",
                previous = "previous",
            )

            val pokemon1 = provideDefaultPokemonTest()
            val expectedPokemons = listOf(pokemon1)

            coEvery { api.getAbilityResponse(5, 5) } returns abilityResponse
            coEvery { api.getAbilityDetails("url1") } returns Ability(
                pokemon = listOf(
                    AbilityPokemonDetail(
                        pokemon = AbilityInfoPokemon(
                            name = "name1",
                            url = "url1",
                        )
                    )
                )
            )
            coEvery { api.getPokemon("url1") } returns pokemon1

            // When
            val result = repository.getPokemons()

            // Then
            Assert.assertEquals(expectedPokemons, result)
        }
    }

    @Test(expected = IOException::class)
    fun `test getPokemons IO exception`() = runBlocking {
        // Given
        coEvery { api.getAbilityResponse(5, 5) } throws IOException()

        // When
        repository.getPokemons()

        // Then
        // IOException should be thrown
        Unit
    }

    @Test(expected = HttpException::class)
    fun `test getPokemons HTTP exception`() = runBlocking {
        // Given
        coEvery { api.getAbilityResponse(5, 5) } throws HttpException(mockk(relaxed = true))

        // When
        repository.getPokemons()

        // Then
        // HttpException should be thrown
        Unit
    }

    @Test
    fun `test getPokemon success`() = runBlocking {
        // Given
        val id = "1"
        val pokemon = provideDefaultPokemonTest()

        coEvery { api.getPokemonById(id) } returns pokemon

        // When
        val result = repository.getPokemon(id)

        // Then
        Assert.assertEquals(pokemon, result)
    }

    @Test(expected = IOException::class)
    fun `test getPokemon IO exception`() = runBlocking {
        // Given
        val id = "1"
        coEvery { api.getPokemonById(id) } throws IOException()

        // When
        repository.getPokemon(id)

        // Then
        // IOException should be thrown
        Unit
    }

    @Test(expected = HttpException::class)
    fun `test getPokemon HTTP exception`() = runBlocking {
        // Given
        val id = "1"
        coEvery { api.getPokemonById(id) } throws HttpException(mockk(relaxed = true))

        // When
        repository.getPokemon(id)

        // Then
        // HttpException should be thrown
        Unit
    }
}