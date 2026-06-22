package com.example.crew_wiki

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import org.jetbrains.compose.resources.Font

import crewwiki.shared.generated.resources.Res
import crewwiki.shared.generated.resources.bm_hanna_pro
import crewwiki.shared.generated.resources.pretendard_bold
import crewwiki.shared.generated.resources.pretendard_medium
import crewwiki.shared.generated.resources.pretendard_regular
import crewwiki.shared.generated.resources.pretendard_semibold

@Composable
fun CrewWikiTheme(content: @Composable () -> Unit) {
    val palette = CrewWikiLightPalette
    val spacing = CrewWikiSpacing()
    val radius = CrewWikiRadius()
    val componentSize = CrewWikiComponentSize()
    val pretendard = FontFamily(
        Font(Res.font.pretendard_regular, FontWeight.Normal),
        Font(Res.font.pretendard_medium, FontWeight.Medium),
        Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
        Font(Res.font.pretendard_bold, FontWeight.Bold),
    )
    val bmHanna = FontFamily(
        Font(Res.font.bm_hanna_pro, FontWeight.Normal),
    )

    CompositionLocalProvider(
        LocalCrewWikiPalette provides palette,
        LocalCrewWikiSpacing provides spacing,
        LocalCrewWikiRadius provides radius,
        LocalCrewWikiComponentSize provides componentSize,
    ) {
        MaterialTheme(
            colorScheme = CrewWikiLightColorScheme,
            typography = crewWikiTypography(
                pretendard = pretendard,
                bmHanna = bmHanna,
            ),
            shapes = crewWikiShapes(radius),
            content = content,
        )
    }
}

private fun crewWikiTypography(
    pretendard: FontFamily,
    bmHanna: FontFamily,
): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = bmHanna,
            fontWeight = FontWeight.Normal,
            fontSize = 40.sp,
            lineHeight = 48.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = bmHanna,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = bmHanna,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 14.sp,
        ),
    )
}

private fun crewWikiShapes(radius: CrewWikiRadius): Shapes {
    return Shapes(
        small = RoundedCornerShape(radius.sm),
        medium = RoundedCornerShape(radius.md),
        large = RoundedCornerShape(radius.lg),
    )
}
