package com.example.crew_wiki.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.RecentDocument
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.document.MarkdownContent
import com.example.crew_wiki.ui.document.preprocessMarkdown

@Composable
fun HomeScreen(
    mainDocument: CrewWikiDocumentDetail?,
    recentDocuments: List<RecentDocument>,
    onDocumentClick: (RecentDocument) -> Unit,
    onPopularClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── 대문 ──────────────────────────────────────────────────────────────
        if (mainDocument != null) {
            item {
                CrewWikiSurfaceSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    val content = mainDocument.document.contents.preprocessMarkdown()
                    if (content.isNotBlank()) {
                        MarkdownContent(
                            content = content,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        // ── 빠른 이동 ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickNavChip(
                    label = "인기문서",
                    onClick = onPopularClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── 최근 편집 섹션 ─────────────────────────────────────────────────────
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Column {
                    // 헤더
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "최근 편집",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.grayscale.c800,
                        )
                    }
                    HorizontalDivider(color = colors.primary.c100)

                    if (recentDocuments.isEmpty()) {
                        Text(
                            text = "편집된 문서가 없습니다.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.grayscale.c500,
                        )
                    } else {
                        recentDocuments.forEach { doc ->
                            RecentDocumentItem(
                                document = doc,
                                onClick = { onDocumentClick(doc) },
                            )
                            HorizontalDivider(color = colors.grayscale.c100)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickNavChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    Row(
        modifier = modifier
            .background(colors.primary.c50, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.primary.c800,
        )
    }
}

@Composable
private fun RecentDocumentItem(
    document: RecentDocument,
    onClick: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val dateLabel = document.generateTime.formatToDate()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "[$dateLabel] ${document.title}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.grayscale.c800,
            modifier = Modifier.weight(1f),
        )
        if (document.documentType == "ORGANIZATION") {
            Text(
                text = "그룹",
                style = MaterialTheme.typography.labelSmall,
                color = colors.primary.c600,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

// "2026-06-22T10:54:00" → "2026.06.22"
private fun String.formatToDate(): String = try {
    val datePart = substringBefore("T")
    datePart.replace("-", ".")
} catch (_: Exception) {
    this
}
