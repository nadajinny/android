package com.example.crew_wiki.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens

enum class CrewWikiActionButtonStyle {
    Primary,
    Secondary,
    Tertiary,
}

@Composable
fun CrewWikiActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CrewWikiActionButtonStyle = CrewWikiActionButtonStyle.Primary,
) {
    val colors = CrewWikiDesignTokens.colors
    val radius = CrewWikiDesignTokens.radius
    val componentSize = CrewWikiDesignTokens.components

    val containerColor = when (style) {
        CrewWikiActionButtonStyle.Primary -> colors.primary.base
        CrewWikiActionButtonStyle.Secondary -> colors.white
        CrewWikiActionButtonStyle.Tertiary -> colors.white
    }
    val contentColor = when (style) {
        CrewWikiActionButtonStyle.Primary -> colors.white
        CrewWikiActionButtonStyle.Secondary -> colors.primary.base
        CrewWikiActionButtonStyle.Tertiary -> colors.grayscale.lightText
    }
    val border = when (style) {
        CrewWikiActionButtonStyle.Primary -> null
        CrewWikiActionButtonStyle.Secondary -> BorderStroke(1.dp, colors.primary.base)
        CrewWikiActionButtonStyle.Tertiary -> BorderStroke(1.dp, colors.grayscale.border)
    }

    Card(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = componentSize.buttonXsHeight),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(radius.pillXs),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor,
            )
        }
    }
}
