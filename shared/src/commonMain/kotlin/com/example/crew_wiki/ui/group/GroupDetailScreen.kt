package com.example.crew_wiki.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.GroupDocumentDetail
import com.example.crew_wiki.model.LinkedCrewDocument
import com.example.crew_wiki.model.OrganizationEvent
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip
import com.example.crew_wiki.ui.document.parseGroupSections

@Composable
fun GroupDetailScreen(
    detail: GroupDocumentDetail,
    onCrewDocumentClick: (uuid: String) -> Unit,
    onLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GroupDetailContent(
        detail = detail,
        onCrewDocumentClick = onCrewDocumentClick,
        onLogsClick = onLogsClick,
        modifier = modifier,
    )
}

@Composable
private fun GroupDetailContent(
    detail: GroupDocumentDetail,
    onCrewDocumentClick: (uuid: String) -> Unit,
    onLogsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing
    val sections = remember(detail.contents) { parseGroupSections(detail.contents) }

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
                    // 헤더
                    GroupDocumentHeader(
                        title = detail.title,
                        onLogsClick = onLogsClick,
                    )

                    // 목차
                    if (sections.isNotEmpty()) {
                        GroupTableOfContents(sections = sections)
                    }

                    // 본문
                    GroupDocumentBody(sections = sections)

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
                        .padding(horizontal = 24.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
                        Text(
                            text = "타임라인",
                            style = MaterialTheme.typography.headlineMedium,
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

        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Text(
                    text = "이 문서는 ${detail.generateTime.formatDate()} 에 마지막으로 편집되었습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.grayscale.text,
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun GroupDocumentHeader(
    title: String,
    onLogsClick: () -> Unit,
) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Card(
                modifier = Modifier.clickable(onClick = onLogsClick),
                colors = CardDefaults.cardColors(
                    containerColor = colors.grayscale.c50,
                ),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = "편집기록",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.grayscale.c700,
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            color = colors.grayscale.c800,
        )
    }
}

@Composable
private fun GroupTableOfContents(sections: List<GroupSectionUiModel>) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    CrewWikiSurfaceSection(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "목차",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
            )
            sections.forEachIndexed { index, section ->
                Text(
                    text = "${index + 1}. ${section.heading}",
                    modifier = Modifier.padding(start = ((section.level - 1) * 16).dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.grayscale.c800,
                )
            }
        }
    }
}

@Composable
private fun GroupDocumentBody(sections: List<GroupSectionUiModel>) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
        sections.forEach { section ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                Text(
                    text = section.heading,
                    style = when (section.level) {
                        1 -> MaterialTheme.typography.headlineMedium
                        2 -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    },
                    color = colors.grayscale.c800,
                )
                section.paragraphs.forEach { paragraph ->
                    Text(
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.grayscale.text,
                    )
                }
            }
        }
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.primary.c100),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = event.occurredAt.formatDate(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primary.c800,
                )
            }
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

// "2026-06-22T10:54:00" → "2026.06.22"
private fun String.formatDate(): String = try {
    substringBefore("T").replace("-", ".")
} catch (e: Exception) { this }

data class GroupSectionUiModel(
    val level: Int,
    val heading: String,
    val paragraphs: List<String>,
)
