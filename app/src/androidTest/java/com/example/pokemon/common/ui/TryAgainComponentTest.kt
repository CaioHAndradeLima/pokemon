package com.example.pokemon.common.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokemon.common.resource.UiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
internal class TryAgainComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenOpenTryAgain_shouldShowMessage() = runTest {
        composeTestRule.setContent {
            TryAgainComponent(errorMessage = UiText.Dynamic(text = "message"))
        }

        composeTestRule.onNodeWithText("message").assertExists()
        throw Exception("testing pipeline error")
    }
}