package com.example.pokemon.common.extension

import com.example.pokemon.R
import com.example.pokemon.common.resource.UiText
import retrofit2.HttpException

fun HttpException.toErrorMessage(): UiText {
    return if (localizedMessage.isNullOrEmpty()) {
        UiText.Resource(R.string.an_unexpected_error_occurred)
    } else {
        UiText.Dynamic(localizedMessage)
    }
}