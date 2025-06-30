package com.example.pokemon.domain.usecase

import com.example.pokemon.R
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.ResponseApi
import com.example.pokemon.data.repository.PokemonApiRepository
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PokemonUseCaseTest {

    private val repository: PokemonApiRepository = mockk()
    private val pokemonUseCase = PokemonUseCase(repository)

    @Test
    fun `test PokemonUseCase emits loading and then success`() {
        // Given
        val fakePokemon = provideDefaultPokemonTest()
        val id = "1"
        every { repository.getPokemon(id) } returns Single.just(ResponseApi.Success(fakePokemon))

        // When
        val testObserver = pokemonUseCase(id).test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val success = testObserver.values()[1] as RequestResource.Success
        assertEquals(fakePokemon, success.data)
    }

    @Test
    fun `test PokemonUseCase emits error when HTTP exception occurs`() {
        // Given
        val id = "1"
        val message = "error message"
        val response = mockk<Response<*>> {
            every { code() } returns 500
            every { message() } returns message
        }

        val exception = HttpException(response)
        val uiText = UiText.Dynamic(exception.localizedMessage ?: "Unknown error")
        every { repository.getPokemon(id) } returns Single.just(ResponseApi.Error.Http(uiText))

        // When
        val testObserver = pokemonUseCase(id).test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val error = testObserver.values()[1] as RequestResource.Error
        assertEquals(uiText, error.message)
    }

    @Test
    fun `test PokemonUseCase emits error when IO exception occurs`() {
        // Given
        val id = "1"
        val expectedUiText = UiText.Resource(R.string.check_your_internet_connection)
        every { repository.getPokemon(id) } returns Single.just(
            ResponseApi.Error.Connection(
                expectedUiText
            )
        )

        // When
        val testObserver = pokemonUseCase(id).test()

        // Then
        testObserver.assertValueCount(2)
        assertTrue(testObserver.values()[0] is RequestResource.Loading)
        val error = testObserver.values()[1] as RequestResource.Error
        assertEquals(expectedUiText, error.message)
    }
}
