package com.example.crew_wiki

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.crew_wiki.navigation.CrewWikiNavRoot

@Composable
@Preview
fun App() {
    CrewWikiTheme {
        CrewWikiNavRoot()
    }
}
