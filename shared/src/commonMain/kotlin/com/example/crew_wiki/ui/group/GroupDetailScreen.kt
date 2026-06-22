package com.example.crew_wiki.ui.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.crew_wiki.model.GroupDocumentDetail
import com.example.crew_wiki.model.LinkedCrewDocument
import com.example.crew_wiki.model.OrganizationEvent
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip
import com.example.crew_wiki.ui.document.MarkdownContent
import com.example.crew_wiki.ui.document.preprocessMarkdown

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupDetailScreen(
    detail: GroupDocumentDetail,
    onCrewDocumentClick: (uuid: String) -> Unit,
    onLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        // 메인 카드 (헤더 + 본문 + 연관 문서)
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xl),
                ) {
                    // 헤더 — 제목(왼쪽) + 편집기록 버튼(오른쪽)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = detail.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.grayscale.c800,
                            modifier = Modifier.weight(1f).padding(end = spacing.md),
                        )
                        CrewWikiActionButton(
                            text = "편집기록",
                            onClick = onLogsClick,
                            style = CrewWikiActionButtonStyle.Tertiary,
                        )
                    }

                    // 본문 마크다운 (자체 렌더러)
                    val content = detail.contents.preprocessMarkdown()
                    if (content.isNotBlank()) {
                        MarkdownContent(
                            content = content,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // 연관 크루 문서
                    if (detail.linkedCrewDocuments.isNotEmpty()) {
                        LinkedCrewSection(
                            crews = detail.linkedCrewDocuments,
                            onCrewClick = onCrewDocumentClick,
                        )
                    }
                }
            }
        }

        // 타임라인
        if (detail.events.isNotEmpty()) {
            item {
                CrewWikiSurfaceSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(spacing.lg),
                    ) {
                        Text(
                            text = "타임라인",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.grayscale.c800,
                        )
                        detail.events.forEach { event ->
                            TimelineEventCard(event = event)
                        }
                    }
                }
            }
        }

        // 푸터
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "이 문서는 ${detail.generateTime.formatGroupDate()}에 마지막으로 편집되었습니다.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.grayscale.c600,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun LinkedCrewSection(
    crews: List<LinkedCrewDocument>,
    onCrewClick: (uuid: String) -> Unit,
) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = "연관 크루 문서",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.grayscale.c800,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            crews.forEach { crew ->
                CrewWikiTagChip(
                    text = crew.title,
                    modifier = Modifier.clickable { onCrewClick(crew.documentUuid) },
                )
            }
        }
    }
}

@Composable
private fun TimelineEventCard(event: OrganizationEvent) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.primary.c100),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = event.occurredAt.formatGroupDate(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = colors.primary.c800,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.grayscale.c800,
            )
            if (event.contents.isNotBlank()) {
                Text(
                    text = event.contents,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.grayscale.c600,
                )
            }
            Text(
                text = "작성: ${event.writer}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.grayscale.c400,
            )
        }
    }
}

private fun String.formatGroupDate(): String = try {
    substringBefore("T").replace("-", ".")
} catch (_: Exception) { this }
