package com.example.crew_wiki.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens

@Composable
fun CrewWikiTagChip(
    text: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val radius = CrewWikiDesignTokens.radius

    Box(
        modifier = modifier
            .background(
                color = colors.primary.c50,
                shape = RoundedCornerShape(radius.pillXs),
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = colors.primary.c800,
        )
    }
}
