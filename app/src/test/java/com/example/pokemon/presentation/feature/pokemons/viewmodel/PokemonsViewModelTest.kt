package com.example.pokemon.presentation.feature.pokemons.viewmodel

import com.example.pokemon.assertInstanceOf
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.domain.usecase.PokemonsUseCase
import com.example.pokemon.flow.testIn
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonsViewModelTest {
    private lateinit var pokemonsUseCase: PokemonsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        pokemonsUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `should update pokemonsState on successful pokemon list retrieval`() = runTest {
        // Arrange
        val pokemonListMock = listOf(provideDefaultPokemonTest())
        val successResult = RequestResource.Success(pokemonListMock)
        coEvery { pokemonsUseCase() } returns flow {
            emit(RequestResource.Loading())
            emit(successResult)
        }

        PokemonsViewModel(pokemonsUseCase).pokemonsState.testIn(this) {
            assertInstanceOf<PokemonsState.Loading>(it.first)
            assertEquals(pokemonListMock, (it.last as PokemonsState.Show).pokemons)
        }
    }

    @Test
    fun `should update pokemonsState on error during pokemon list retrieval`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        val errorResult = RequestResource.Error<List<Pokemon>>(message = uiText)
        coEvery { pokemonsUseCase() } returns flow {
            emit(RequestResource.Loading())
            emit(errorResult)
        }

        PokemonsViewModel(pokemonsUseCase).pokemonsState.testIn(this) {
            assertInstanceOf<PokemonsState.Loading>(it.first)
            assertEquals(uiText, (it.last as PokemonsState.TryAgain).errorMessage)
        }
    }
}
