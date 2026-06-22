package com.example.crew_wiki

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.setSingletonImageLoaderFactory
import com.example.crew_wiki.navigation.CrewWikiNavRoot

@Composable
@Preview
fun App() {
    // Coil3 싱글톤 초기화 (네트워크 이미지 로딩)
    setSingletonImageLoaderFactory { context ->
        coil3.ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    CrewWikiTheme {
        CrewWikiNavRoot()
    }
}
