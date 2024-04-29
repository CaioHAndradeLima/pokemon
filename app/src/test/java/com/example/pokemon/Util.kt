package com.example.pokemon

import com.example.pokemon.common.resource.UiText

fun getIOExceptionMessage() = UiText.Resource(R.string.check_your_internet_connection)

fun getHttpExceptionMessage(
    message: String?
) = when {
    message != null -> UiText.Dynamic(message)
    else -> UiText.Resource(R.string.an_unexpected_error_occurred)
}