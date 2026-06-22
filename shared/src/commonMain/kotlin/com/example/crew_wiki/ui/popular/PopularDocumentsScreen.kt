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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection

private enum class SortTab(val displayName: String, val label: String) {
    VIEWS("조회수", "views"),
    EDITS("수정수", "edits"),
}

// crew-wiki-next PopularPage 레이아웃 그대로 구현
// ※ API에 editCount 미제공 → 수정수 탭도 viewCount 표시
@Composable
fun PopularDocumentsScreen(
    documents: List<PopularDocument>,
    onDocumentClick: (PopularDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sortTab by remember { mutableStateOf(SortTab.VIEWS) }
    val topTen = documents.take(10)
    val topThree = topTen.take(3)
    val remaining = topTen.drop(3)
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xl),
                ) {
                    // 헤더 - web의 PopularHeader
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "인기문서",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = CrewWikiDesignTokens.colors.grayscale.c800,
                        )
                        // 필터 버튼 - web의 PopularFilterButtons
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            SortTab.entries.forEach { tab ->
                                CrewWikiActionButton(
                                    text = tab.displayName,
                                    onClick = { sortTab = tab },
                                    style = if (sortTab == tab) CrewWikiActionButtonStyle.Primary
                                    else CrewWikiActionButtonStyle.Tertiary,
                                )
                            }
                        }
                    }

                    // 상위 3개 - web의 ol.grid-cols-3
                    BoxWithConstraints {
                        if (maxWidth >= 600.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                            ) {
                                topThree.forEachIndexed { index, doc ->
                                    PopularRankingCard(
                                        rank = index + 1,
                                        document = doc,
                                        sortTab = sortTab,
                                        onClick = { onDocumentClick(doc) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                                topThree.forEachIndexed { index, doc ->
                                    PopularRankingCard(
                                        rank = index + 1,
                                        document = doc,
                                        sortTab = sortTab,
                                        onClick = { onDocumentClick(doc) },
                                    )
                                }
                            }
                        }
                    }

                    // 4~10위 - web의 PopularRemainingDocuments
                    PopularRemainingList(
                        documents = remaining,
                        startRank = 4,
                        sortTab = sortTab,
                        onDocumentClick = onDocumentClick,
                    )
                }
            }
        }
    }
}

// web: PopularRankingCard
@Composable
private fun PopularRankingCard(
    rank: Int,
    document: PopularDocument,
    sortTab: SortTab,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val rankEmojis = listOf("🥇", "🥈", "🥉")
    val primaryLabel = sortTab.displayName
    val primaryCount = document.viewCount   // API에 editCount 없음
    val secondaryLabel = if (sortTab == SortTab.VIEWS) "수정수" else "조회수"
    val secondaryCount = document.viewCount // API에 editCount 없음

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
            // 순위 - web: flex items-center gap-3
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
                    fontWeight = FontWeight.SemiBold,
                    color = colors.grayscale.c600,
                )
            }
            // 제목
            Text(
                text = document.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
            )
            // 통계 - web: flex flex-col gap-2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricRow(
                    label = primaryLabel,
                    value = primaryCount.toLocaleString(),
                    emphasized = true,
                )
                MetricRow(
                    label = secondaryLabel,
                    value = secondaryCount.toLocaleString(),
                    emphasized = false,
                )
            }
        }
    }
}

// web: PopularRemainingDocuments + PopularDocumentItem
@Composable
private fun PopularRemainingList(
    documents: List<PopularDocument>,
    startRank: Int,
    sortTab: SortTab,
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
        documents.forEachIndexed { index, doc ->
            PopularDocumentItem(
                rank = startRank + index,
                document = doc,
                sortTab = sortTab,
                onClick = { onDocumentClick(doc) },
            )
            if (index < documents.lastIndex) {
                HorizontalDivider(color = colors.grayscale.c100)
            }
        }
    }
}

// web: PopularDocumentItem
@Composable
private fun PopularDocumentItem(
    rank: Int,
    document: PopularDocument,
    sortTab: SortTab,
    onClick: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val primaryCount = document.viewCount    // API에 editCount 없음
    val secondaryCount = document.viewCount  // API에 editCount 없음
    val primaryLabel = if (sortTab == SortTab.VIEWS) "조회" else "수정"
    val secondaryLabel = if (sortTab == SortTab.VIEWS) "수정" else "조회"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 순위 번호
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.grayscale.c600,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        // 제목
        Text(
            text = document.title,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            color = colors.grayscale.c800,
        )
        // 주요 통계
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = primaryCount.toLocaleString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.grayscale.c800,
            )
            Text(
                text = primaryLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.grayscale.c500,
            )
        }
        // 보조 통계 (수정수 / 조회수)
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = secondaryCount.toLocaleString(),
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.c600,
            )
            Text(
                text = secondaryLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.grayscale.c500,
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, emphasized: Boolean) {
    val colors = CrewWikiDesignTokens.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.grayscale.c600,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            color = if (emphasized) colors.grayscale.c800 else colors.grayscale.c500,
        )
    }
}

private fun Int.toLocaleString(): String =
    toString().reversed().chunked(3).joinToString(",").reversed()
