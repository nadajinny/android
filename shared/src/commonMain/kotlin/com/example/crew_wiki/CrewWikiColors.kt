package com.example.crew_wiki

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CrewWikiColorScale(
    val c50: Color,
    val c100: Color,
    val c200: Color,
    val c300: Color,
    val c400: Color,
    val c500: Color,
    val c600: Color,
    val c700: Color,
    val c800: Color,
    val c900: Color,
    val base: Color,
    val container: Color,
    val onContainer: Color,
)

@Immutable
data class CrewWikiErrorScale(
    val c50: Color,
    val c100: Color,
    val c200: Color,
    val c300: Color,
    val c400: Color,
    val c500: Color,
    val c600: Color,
    val c700: Color,
    val c800: Color,
    val c900: Color,
    val base: Color,
    val container: Color,
)

@Immutable
data class CrewWikiGrayscaleScale(
    val c50: Color,
    val c100: Color,
    val c200: Color,
    val c300: Color,
    val c400: Color,
    val c500: Color,
    val c600: Color,
    val c700: Color,
    val c800: Color,
    val c900: Color,
    val container: Color,
    val border: Color,
    val lightText: Color,
    val text: Color,
)

@Immutable
data class CrewWikiPalette(
    val black: Color,
    val white: Color,
    val primary: CrewWikiColorScale,
    val secondary: CrewWikiColorScale,
    val error: CrewWikiErrorScale,
    val grayscale: CrewWikiGrayscaleScale,
)

internal val CrewWikiLightPalette = CrewWikiPalette(
    black = Color(0xFF000000),
    white = Color(0xFFFFFFFF),
    primary = CrewWikiColorScale(
        c50 = Color(0xFFDEF2F4),
        c100 = Color(0xFFACDEE1),
        c200 = Color(0xFF72C9CE),
        c300 = Color(0xFF25B4B9),
        c400 = Color(0xFF00A4A8),
        c500 = Color(0xFF009495),
        c600 = Color(0xFF008787),
        c700 = Color(0xFF007776),
        c800 = Color(0xFF006766),
        c900 = Color(0xFF004B47),
        base = Color(0xFF25B4B9),
        container = Color(0xFFDEF2F4),
        onContainer = Color(0xFF006766),
    ),
    secondary = CrewWikiColorScale(
        c50 = Color(0xFFF6E3F4),
        c100 = Color(0xFFE7B8E4),
        c200 = Color(0xFFD788D3),
        c300 = Color(0xFFC655C1),
        c400 = Color(0xFFB925B4),
        c500 = Color(0xFFAB00A8),
        c600 = Color(0xFF9D00A3),
        c700 = Color(0xFF8A009D),
        c800 = Color(0xFF790097),
        c900 = Color(0xFF58008B),
        base = Color(0xFFB925B4),
        container = Color(0xFFF6E3F4),
        onContainer = Color(0xFF58008B),
    ),
    error = CrewWikiErrorScale(
        c50 = Color(0xFFFFECEF),
        c100 = Color(0xFFFFCFD4),
        c200 = Color(0xFFF09E9E),
        c300 = Color(0xFFE67979),
        c400 = Color(0xFFF15B57),
        c500 = Color(0xFFF64C3E),
        c600 = Color(0xFFE8433E),
        c700 = Color(0xFFD53A37),
        c800 = Color(0xFFC83430),
        c900 = Color(0xFFB92A25),
        base = Color(0xFFD53A37),
        container = Color(0xFFFFECEF),
    ),
    grayscale = CrewWikiGrayscaleScale(
        c50 = Color(0xFFF3F4F6),
        c100 = Color(0xFFE3E3E7),
        c200 = Color(0xFFD9DADC),
        c300 = Color(0xFFC7C8CA),
        c400 = Color(0xFF9FA0A2),
        c500 = Color(0xFF77787A),
        c600 = Color(0xFF4F5052),
        c700 = Color(0xFF36383D),
        c800 = Color(0xFF27282A),
        c900 = Color(0xFF18191A),
        container = Color(0xFFF3F4F6),
        border = Color(0xFFE3E3E7),
        lightText = Color(0xFF9FA0A2),
        text = Color(0xFF27282A),
    ),
)

internal val LocalCrewWikiPalette = staticCompositionLocalOf { CrewWikiLightPalette }

internal val CrewWikiLightColorScheme = lightColorScheme(
    primary = CrewWikiLightPalette.primary.base,
    onPrimary = CrewWikiLightPalette.white,
    primaryContainer = CrewWikiLightPalette.primary.container,
    onPrimaryContainer = CrewWikiLightPalette.primary.onContainer,
    secondary = CrewWikiLightPalette.secondary.base,
    onSecondary = CrewWikiLightPalette.white,
    secondaryContainer = CrewWikiLightPalette.secondary.container,
    onSecondaryContainer = CrewWikiLightPalette.secondary.onContainer,
    tertiary = CrewWikiLightPalette.grayscale.c700,
    onTertiary = CrewWikiLightPalette.white,
    tertiaryContainer = CrewWikiLightPalette.grayscale.c50,
    onTertiaryContainer = CrewWikiLightPalette.grayscale.text,
    background = CrewWikiLightPalette.grayscale.container,
    onBackground = CrewWikiLightPalette.grayscale.text,
    surface = CrewWikiLightPalette.white,
    onSurface = CrewWikiLightPalette.grayscale.text,
    surfaceVariant = CrewWikiLightPalette.grayscale.c50,
    onSurfaceVariant = CrewWikiLightPalette.grayscale.c600,
    outline = CrewWikiLightPalette.grayscale.border,
    outlineVariant = CrewWikiLightPalette.grayscale.c100,
    error = CrewWikiLightPalette.error.base,
    onError = CrewWikiLightPalette.white,
    errorContainer = CrewWikiLightPalette.error.container,
    onErrorContainer = CrewWikiLightPalette.error.c900,
    surfaceTint = CrewWikiLightPalette.primary.base,
)
