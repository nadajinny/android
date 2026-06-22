package com.example.crew_wiki

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

import crewwiki.shared.generated.resources.Res
import crewwiki.shared.generated.resources.bm_hanna_pro
import crewwiki.shared.generated.resources.pretendard_bold
import crewwiki.shared.generated.resources.pretendard_medium
import crewwiki.shared.generated.resources.pretendard_regular
import crewwiki.shared.generated.resources.pretendard_semibold

@Composable
fun CrewWikiTheme(content: @Composable () -> Unit) {
    val pretendard = FontFamily(
        Font(Res.font.pretendard_regular, FontWeight.Normal),
        Font(Res.font.pretendard_medium, FontWeight.Medium),
        Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
        Font(Res.font.pretendard_bold, FontWeight.Bold),
    )
    val bmHanna = FontFamily(
        Font(Res.font.bm_hanna_pro, FontWeight.Normal),
    )

    MaterialTheme(
        typography = crewWikiTypography(
            pretendard = pretendard,
            bmHanna = bmHanna,
        ),
        content = content,
    )
}

private fun crewWikiTypography(
    pretendard: FontFamily,
    bmHanna: FontFamily,
): Typography {
    val defaultTypography = Typography()

    fun TextStyle.withFont(fontFamily: FontFamily): TextStyle = copy(fontFamily = fontFamily)

    return defaultTypography.copy(
        displayLarge = defaultTypography.displayLarge.withFont(bmHanna),
        displayMedium = defaultTypography.displayMedium.withFont(bmHanna),
        displaySmall = defaultTypography.displaySmall.withFont(bmHanna),
        headlineLarge = defaultTypography.headlineLarge.withFont(pretendard),
        headlineMedium = defaultTypography.headlineMedium.withFont(pretendard),
        headlineSmall = defaultTypography.headlineSmall.withFont(pretendard),
        titleLarge = defaultTypography.titleLarge.withFont(pretendard),
        titleMedium = defaultTypography.titleMedium.withFont(pretendard),
        titleSmall = defaultTypography.titleSmall.withFont(pretendard),
        bodyLarge = defaultTypography.bodyLarge.withFont(pretendard),
        bodyMedium = defaultTypography.bodyMedium.withFont(pretendard),
        bodySmall = defaultTypography.bodySmall.withFont(pretendard),
        labelLarge = defaultTypography.labelLarge.withFont(pretendard),
        labelMedium = defaultTypography.labelMedium.withFont(pretendard),
        labelSmall = defaultTypography.labelSmall.withFont(pretendard),
    )
}
