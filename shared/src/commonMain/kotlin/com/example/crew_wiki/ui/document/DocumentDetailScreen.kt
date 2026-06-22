package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip

@Composable
fun DocumentDetailScreen(
    documentDetail: CrewWikiDocumentDetail,
    modifier: Modifier = Modifier,
) {
    val uiState = rememberDocumentDetailUiState(documentDetail)
    val colors = CrewWikiDesignTokens.colors
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
                    DocumentDetailHeader(
                        title = uiState.title,
                        onEditClick = {},
                        onLogsClick = {},
                        onWriteClick = {},
                    )
                    TableOfContentsCard(sections = uiState.sections)
                    CrewSection(
                        title = "함께 보는 크루",
                        crews = uiState.relatedCrewNames,
                    )
                    DocumentBody(sections = uiState.sections)
                }
            }
        }
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text(
                        text = "이 문서는 ${uiState.lastEditedLabel} 에 마지막으로 편집되었습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.text,
                    )
                    Text(
                        text = "질문, 제안, 오류 제보는 문의하기 채널을 이용해 주세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.text,
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentDetailHeader(
    title: String,
    onEditClick: () -> Unit,
    onLogsClick: () -> Unit,
    onWriteClick: () -> Unit,
) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            CrewWikiActionButton(
                text = "편집하기",
                onClick = onEditClick,
                style = CrewWikiActionButtonStyle.Tertiary,
            )
            CrewWikiActionButton(
                text = "편집기록",
                onClick = onLogsClick,
                style = CrewWikiActionButtonStyle.Tertiary,
            )
            CrewWikiActionButton(
                text = "작성하기",
                onClick = onWriteClick,
                style = CrewWikiActionButtonStyle.Primary,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium,
            color = colors.grayscale.c800,
        )
    }
}

@Composable
private fun TableOfContentsCard(
    sections: List<DocumentSectionUiModel>,
) {
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
                val indentation = ((section.level - 1) * 16).dp
                Text(
                    text = "${index + 1}. ${section.heading}",
                    modifier = Modifier.padding(start = indentation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.grayscale.c800,
                )
            }
        }
    }
}

@Composable
private fun CrewSection(
    title: String,
    crews: List<String>,
) {
    if (crews.isEmpty()) return

    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.grayscale.c800,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            crews.forEach { crew ->
                CrewWikiTagChip(text = crew)
            }
        }
    }
}

@Composable
private fun DocumentBody(
    sections: List<DocumentSectionUiModel>,
) {
    val spacing = CrewWikiDesignTokens.spacing
    val colors = CrewWikiDesignTokens.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
        sections.forEachIndexed { index, section ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                Text(
                    text = "${index + 1}. ${section.heading}",
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

@Immutable
private data class DocumentDetailUiState(
    val title: String,
    val relatedCrewNames: List<String>,
    val lastEditedLabel: String,
    val sections: List<DocumentSectionUiModel>,
)

@Immutable
private data class DocumentSectionUiModel(
    val level: Int,
    val heading: String,
    val paragraphs: List<String>,
)

@Composable
private fun rememberDocumentDetailUiState(documentDetail: CrewWikiDocumentDetail): DocumentDetailUiState {
    return remember(documentDetail) {
        val document = documentDetail.document

        DocumentDetailUiState(
            title = document.title,
            relatedCrewNames = documentDetail.relatedCrewDocuments.map { it.title },
            lastEditedLabel = document.generateTime,
            sections = parseDocumentSections(document.contents),
        )
    }
}

private fun parseDocumentSections(contents: String): List<DocumentSectionUiModel> {
    val sections = mutableListOf<DocumentSectionUiModel>()
    var currentLevel = 1
    var currentHeading = "문서"
    val currentParagraphs = mutableListOf<String>()

    fun flushSection() {
        val paragraphs = currentParagraphs
            .map(String::trim)
            .filter(String::isNotEmpty)
        if (currentHeading.isNotBlank() || paragraphs.isNotEmpty()) {
            sections += DocumentSectionUiModel(
                level = currentLevel,
                heading = currentHeading,
                paragraphs = if (paragraphs.isEmpty()) listOf("내용이 아직 없습니다.") else paragraphs,
            )
        }
        currentParagraphs.clear()
    }

    contents.lineSequence().forEach { rawLine ->
        val line = rawLine.trim()
        when {
            line.startsWith("### ") -> {
                flushSection()
                currentLevel = 3
                currentHeading = line.removePrefix("### ").trim()
            }
            line.startsWith("## ") -> {
                flushSection()
                currentLevel = 2
                currentHeading = line.removePrefix("## ").trim()
            }
            line.startsWith("# ") -> {
                flushSection()
                currentLevel = 1
                currentHeading = line.removePrefix("# ").trim()
            }
            line.isNotBlank() -> currentParagraphs += line
        }
    }

    flushSection()

    return sections.ifEmpty {
        listOf(
            DocumentSectionUiModel(
                level = 1,
                heading = "문서",
                paragraphs = listOf(contents),
            ),
        )
    }
}
