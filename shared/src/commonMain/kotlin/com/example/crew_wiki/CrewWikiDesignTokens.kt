package com.example.crew_wiki

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class CrewWikiSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
)

@Immutable
data class CrewWikiRadius(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val pillXs: Dp = 18.dp,
    val pillSm: Dp = 22.dp,
    val pillMd: Dp = 28.dp,
    val full: Dp = 999.dp,
)

@Immutable
data class CrewWikiComponentSize(
    val buttonXxsHeight: Dp = 24.dp,
    val buttonXsHeight: Dp = 36.dp,
    val buttonSmHeight: Dp = 44.dp,
    val buttonMdHeight: Dp = 56.dp,
    val fieldHeight: Dp = 44.dp,
    val largeFieldHeight: Dp = 56.dp,
)

internal val LocalCrewWikiSpacing = staticCompositionLocalOf { CrewWikiSpacing() }
internal val LocalCrewWikiRadius = staticCompositionLocalOf { CrewWikiRadius() }
internal val LocalCrewWikiComponentSize = staticCompositionLocalOf { CrewWikiComponentSize() }

object CrewWikiDesignTokens {
    val colors: CrewWikiPalette
        @Composable get() = LocalCrewWikiPalette.current

    val spacing: CrewWikiSpacing
        @Composable get() = LocalCrewWikiSpacing.current

    val radius: CrewWikiRadius
        @Composable get() = LocalCrewWikiRadius.current

    val components: CrewWikiComponentSize
        @Composable get() = LocalCrewWikiComponentSize.current

    val typography
        @Composable get() = MaterialTheme.typography
}
