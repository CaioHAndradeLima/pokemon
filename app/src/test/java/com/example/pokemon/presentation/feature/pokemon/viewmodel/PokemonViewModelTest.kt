package com.example.pokemon.presentation.feature.pokemon.viewmodel

import com.example.pokemon.common.network.RequestResource
import com.example.pokemon.common.resource.UiText
import com.example.pokemon.domain.usecase.PokemonUseCase
import com.example.pokemon.provider.provideDefaultPokemonTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
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
        every { pokemonUseCase(any()) } returns Observable.just(RequestResource.Success(pokemonMock))

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))
        advanceUntilIdle()

        // Assert
        assert(pokemonViewModel.pokemonState.value is PokemonState.Show)
        assertEquals(
            pokemonMock,
            (pokemonViewModel.pokemonState.value as PokemonState.Show).pokemon
        )
    }

    @Test
    fun `should update pokemonState on error during pokemon retrieval`() = runTest {
        // Arrange
        val uiText = UiText.Dynamic("Some error message")
        every { pokemonUseCase(any()) } returns Observable.just(RequestResource.Error(message = uiText))

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))
        advanceUntilIdle()

        // Assert
        assert(pokemonViewModel.pokemonState.value is PokemonState.TryAgain)
        assertEquals(
            uiText,
            (pokemonViewModel.pokemonState.value as PokemonState.TryAgain).errorMessage
        )
    }

    @Test
    fun `should update pokemonState on loading during pokemon retrieval`() = runTest {
        // Arrange
        every { pokemonUseCase(any()) } returns Observable.just(RequestResource.Loading())

        // Act
        pokemonViewModel.on(PokemonEvent.Find("1"))
        advanceUntilIdle()

        // Assert
        assert(pokemonViewModel.pokemonState.value is PokemonState.Loading)
    }
}
