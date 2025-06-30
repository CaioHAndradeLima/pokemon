package com.example.pokemon.domain.usecase

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import com.example.pokemon.provider.providePokemonWithoutPictureTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonsUseCaseTest {

    private val repository = mockk<PokemonApiRepository>()
    private val pokemonsUseCase = PokemonsUseCase(repository)

    @Test
    fun `test PokemonsUseCase emits loading and then success`() {
        // Given
        val pokemon = provideDefaultPokemonTest()
        val fakePokemons = listOf(pokemon)
        every { repository.getPokemons() } returns Single.just(ResponseApi.Success(fakePokemons))

        // When
        val testObserver = pokemonsUseCase().test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val success = testObserver.values()[1] as RequestResource.Success
        assertEquals(fakePokemons, success.data)
    }

    @Test
    fun `test PokemonsUseCase no sprites should emits loading and then success without data`() {
        // Given
        val pokemon = providePokemonWithoutPictureTest()
        every { repository.getPokemons() } returns Single.just(ResponseApi.Success(listOf(pokemon)))

        // When
        val testObserver = pokemonsUseCase().test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val success = testObserver.values()[1] as RequestResource.Success
        assertEquals(emptyList<Pokemon>(), success.data)
    }

    @Test
    fun `test PokemonsUseCase emits error when HTTP error occurs`() {
        // Given
        val errorText = UiText.Dynamic("Server error")
        every { repository.getPokemons() } returns Single.just(ResponseApi.Error.Http(errorText))

        // When
        val testObserver = pokemonsUseCase().test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val error = testObserver.values()[1] as RequestResource.Error
        assertEquals(errorText, error.message)
        assertTrue(error.message is UiText.Dynamic)
    }

    @Test
    fun `test PokemonsUseCase emits error when IO error occurs`() {
        // Given
        val connectionText =
            UiText.Resource(com.example.pokemon.R.string.check_your_internet_connection)
        every { repository.getPokemons() } returns Single.just(
            ResponseApi.Error.Connection(
                connectionText
            )
        )

        // When
        val testObserver = pokemonsUseCase().test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val error = testObserver.values()[1] as RequestResource.Error
        assertEquals(connectionText, error.message)
        assertTrue(error.message is UiText.Resource)
    }
}
