package com.example.crew_wiki

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.example.crew_wiki.navigation.CrewWikiNavRoot

@Composable
@Preview
fun App() {
    // Coil3 + Ktor 네트워크 이미지 로더 초기화
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
    }

    CrewWikiTheme {
        CrewWikiNavRoot()
    }
}
