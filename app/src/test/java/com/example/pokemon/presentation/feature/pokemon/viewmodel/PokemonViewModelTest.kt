package com.example.pokemon.presentation.feature.pokemon.viewmodel

import com.example.pokemon.assertInstanceOf
import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.data.model.Pokemon
import com.example.pokemon.domain.usecase.PokemonUseCase
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    private lateinit var pokemonUseCase: PokemonUseCase
    private lateinit var pokemonViewModel: PokemonViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        pokemonUseCase = mockk(relaxed = true)
        pokemonViewModel = PokemonViewModel(pokemonUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
        clearAllMocks()
    }

    @Test
    fun `should update pokemonState on successful pokemon retrieval`() = runTest {
        // Arrange
        val pokemonMock = provideDefaultPokemonTest()
        val successResult = RequestResource.Success(pokemonMock)
        coEvery { pokemonUseCase(any()) } returns flow {
            emit(RequestResource.Loading())
            emit(successResult)
        }

        val state = pokemonViewModel.getPokemonStateFlow("1").toList()
        // Assert
        assertEquals(2, state.size)
        assertEquals(state.first(), PokemonState.Loading)
        assertInstanceOf<PokemonState.Show>(state.last())
        assertEquals(pokemonMock, (state.last() as PokemonState.Show).pokemon)
    }

    @Test
    fun `should update pokemonState on error during pokemon retrieval`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        val errorResult = RequestResource.Error<Pokemon>(message = uiText)
        coEvery { pokemonUseCase(any()) } returns flow {
            emit(RequestResource.Loading())
            emit(errorResult)
        }

        // Act
        val state = pokemonViewModel.getPokemonStateFlow("1").toList()

        // Assert
        assertEquals(2, state.size)
        assertEquals(state.first(), PokemonState.Loading)
        assertEquals(
            uiText,
            (state.last() as PokemonState.TryAgain).errorMessage
        )
    }
}
