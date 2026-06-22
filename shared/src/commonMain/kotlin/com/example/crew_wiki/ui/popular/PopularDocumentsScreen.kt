package com.example.crew_wiki.ui.popular

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection

// Swagger API에 editCount 없음 → viewCount 기준만 지원
@Composable
fun PopularDocumentsScreen(
    documents: List<PopularDocument>,
    onDocumentClick: (PopularDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topThree = documents.take(3)
    val remaining = documents.drop(3)
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
    ) {
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "인기문서",
                            style = MaterialTheme.typography.displayMedium,
                            color = CrewWikiDesignTokens.colors.grayscale.c800,
                        )
                        Text(
                            text = "조회수 기준 상위 10개 문서",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrewWikiDesignTokens.colors.grayscale.c500,
                        )
                    }

                    TopRankingSection(
                        documents = topThree,
                        onDocumentClick = onDocumentClick,
                    )

                    RemainingRankingSection(
                        documents = remaining,
                        startRank = 4,
                        onDocumentClick = onDocumentClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopRankingSection(
    documents: List<PopularDocument>,
    onDocumentClick: (PopularDocument) -> Unit,
) {
    val spacing = CrewWikiDesignTokens.spacing
    BoxWithConstraints {
        if (maxWidth >= 720.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                documents.forEachIndexed { index, document ->
                    TopRankingCard(
                        rank = index + 1,
                        document = document,
                        onClick = { onDocumentClick(document) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                documents.forEachIndexed { index, document ->
                    TopRankingCard(
                        rank = index + 1,
                        document = document,
                        onClick = { onDocumentClick(document) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TopRankingCard(
    rank: Int,
    document: PopularDocument,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val rankEmojis = listOf("🥇", "🥈", "🥉")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, colors.primary.c100),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = rankEmojis.getOrElse(rank - 1) { "🏅" },
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    text = "${rank}위",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.grayscale.c600,
                )
            }
            Text(
                text = document.title,
                style = MaterialTheme.typography.headlineSmall,
                color = colors.grayscale.c800,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "조회수",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.grayscale.c600,
                )
                Text(
                    text = document.viewCount.toDisplayCount(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.grayscale.c800,
                )
            }
        }
    }
}

@Composable
private fun RemainingRankingSection(
    documents: List<PopularDocument>,
    startRank: Int,
    onDocumentClick: (PopularDocument) -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors

    if (documents.isEmpty()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.grayscale.c50),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, colors.grayscale.c100),
        ) {
            Text(
                text = "등록된 문서가 없습니다.",
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.grayscale.c500,
            )
        }
        return
    }

    Column {
        documents.forEachIndexed { index, document ->
            PopularDocumentListItem(
                rank = startRank + index,
                document = document,
                onClick = { onDocumentClick(document) },
            )
        }
    }
}

@Composable
private fun PopularDocumentListItem(
    rank: Int,
    document: PopularDocument,
    onClick: () -> Unit,
) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = colors.grayscale.c600,
        )
        Text(
            text = document.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = colors.grayscale.c800,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = document.viewCount.toDisplayCount(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.grayscale.c800,
            )
            Text(
                text = "조회수",
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.c500,
            )
        }
    }
}

private fun Int.toDisplayCount(): String =
    toString().reversed().chunked(3).joinToString(",").reversed()
