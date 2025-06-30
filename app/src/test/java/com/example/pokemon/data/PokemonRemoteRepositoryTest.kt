package com.example.pokemon.data

import com.example.pokemon.R
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Ability
import com.example.pokemon.data.model.AbilityInfoPokemon
import com.example.pokemon.data.model.AbilityPokemonDetail
import com.example.pokemon.data.model.PokemonAbilityResponse
import com.example.pokemon.data.repository.PokemonApi
import com.example.pokemon.data.repository.PokemonRemoteRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PokemonRemoteRepositoryTest {

    private val api: PokemonApi = mockk()
    private val repository = PokemonRemoteRepository(api)

    @Test
    fun `test getPokemons success`() = runBlocking {
        val abilityResponse = PokemonAbilityResponse(
            results = listOf(AbilityInfoPokemon(name = "name1", url = "url1")),
            count = 1,
            next = "next",
            previous = "previous",
        )

        val pokemon1 = provideDefaultPokemonTest()
        val expectedPokemons = listOf(pokemon1)

        coEvery { api.getAbilityResponse(5, 5) } returns Single.fromCallable { abilityResponse }
        coEvery { api.getAbilityDetails("url1") } returns Single.fromCallable {
            Ability(
                pokemon = listOf(
                    AbilityPokemonDetail(
                        pokemon = AbilityInfoPokemon(
                            name = "name1",
                            url = "url1"
                        )
                    )
                )
            )
        }
        coEvery { api.getPokemon("url1") } returns Single.fromCallable { pokemon1 }

        val result = repository.getPokemons().blockingGet()

        Assert.assertTrue(result is ResponseApi.Success)
        val data = (result as ResponseApi.Success).data
        Assert.assertEquals(expectedPokemons, data)
    }

    @Test
    fun `test getPokemons IO exception`() = runBlocking {
        coEvery { api.getAbilityResponse(5, 5) } throws IOException()

        val result = repository.getPokemons().blockingGet()

        Assert.assertTrue(result is ResponseApi.Error.Connection)
        val error = result as ResponseApi.Error.Connection
        Assert.assertEquals(UiText.Resource(R.string.check_your_internet_connection), error.message)
    }

    @Test
    fun `test getPokemons HTTP exception`() = runBlocking {
        val message = "Internal Server Error"
        val response = mockk<Response<*>> {
            every { code() } returns 500
            every { message() } returns message
        }
        coEvery { api.getAbilityResponse(5, 5) } throws HttpException(response)

        val result = repository.getPokemons().blockingGet()

        Assert.assertTrue(result is ResponseApi.Error.Http)
        val error = result as ResponseApi.Error.Http
        Assert.assertEquals(UiText.Dynamic("HTTP 500 $message"), error.message)
    }

    @Test
    fun `test getPokemon success`() = runBlocking {
        val id = "1"
        val pokemon = provideDefaultPokemonTest()

        coEvery { api.getPokemonById(id) } returns Single.fromCallable { pokemon }

        val result = repository.getPokemon(id).blockingGet()

        Assert.assertTrue(result is ResponseApi.Success)
        val data = (result as ResponseApi.Success).data
        Assert.assertEquals(pokemon, data)
    }

    @Test
    fun `test getPokemon IO exception`() = runBlocking {
        val id = "1"
        coEvery { api.getPokemonById(id) } throws IOException()

        val result = repository.getPokemon(id).blockingGet()

        Assert.assertTrue(result is ResponseApi.Error.Connection)
        val error = result as ResponseApi.Error.Connection
        Assert.assertEquals(UiText.Resource(R.string.check_your_internet_connection), error.message)
    }

    @Test
    fun `test getPokemon HTTP exception`() = runBlocking {
        val id = "1"
        val message = "Not Found"
        val response = mockk<Response<*>> {
            every { code() } returns 404
            every { message() } returns message
        }

        coEvery { api.getPokemonById(id) } throws HttpException(response)

        val result = repository.getPokemon(id).blockingGet()

        Assert.assertTrue(result is ResponseApi.Error.Http)
        val error = result as ResponseApi.Error.Http
        Assert.assertEquals(UiText.Dynamic("HTTP 404 $message"), error.message)
    }
}
