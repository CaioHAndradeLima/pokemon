package com.example.pokemon.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.pokemon.R

@Composable
fun CoilImageComponent(
    imageUrl: String,
    width: Dp = 104.dp,
    boxModifier: Modifier = Modifier
        .clip(shape = RoundedCornerShape(8.dp))
        .background(Color.Gray),
    modifier: Modifier = Modifier
        .height((width.value * 1.36).dp),
    contentScale: ContentScale = ContentScale.Crop
) {

    Box(
        modifier = boxModifier
    ) {

        val listener = object : ImageRequest.Listener {
            override fun onError(request: ImageRequest, result: ErrorResult) {
                super.onError(request, result)
            }

            override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                super.onSuccess(request, result)
            }
        }
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .listener(listener)
            .memoryCacheKey(imageUrl)
            .diskCacheKey(imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        // Load and display the image with AsyncImage
        AsyncImage(
            model = imageRequest,
            contentDescription = stringResource(id = R.string.pokemon_image_accessibility),
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}