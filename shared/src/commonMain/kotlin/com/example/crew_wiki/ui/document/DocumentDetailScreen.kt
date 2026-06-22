package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip

// web: DocumentPage + DocumentHeader + DocumentContents + DocumentFooter
@Composable
fun DocumentDetailScreen(
    documentDetail: CrewWikiDocumentDetail,
    onEditClick: () -> Unit = {},
    onLogsClick: () -> Unit = {},
    onWriteClick: () -> Unit = {},
    onOrganizationClick: (uuid: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState: DocumentDetailScreenState = rememberDocumentDetailUiState(documentDetail)
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        // 메인 카드 - web: section.rounded-xl.border.border-primary-100.bg-white
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
                    // DocumentHeader - web: flex justify-between
                    DocumentDetailHeader(
                        title = uiState.title,
                        onEditClick = onEditClick,
                        onLogsClick = onLogsClick,
                        onWriteClick = onWriteClick,
                    )

                    // 목차 (TOC) - web: TOC component
                    if (uiState.sections.isNotEmpty()) {
                        TableOfContentsSection(sections = uiState.sections)
                    }

                    // 소속 섹션 - web: OrganizationSection (chip list)
                    if (uiState.organizations.isNotEmpty()) {
                        OrganizationSection(
                            organizations = uiState.organizations,
                            onOrganizationClick = onOrganizationClick,
                        )
                    }

                    // 본문 - web: toastui-editor-contents
                    DocumentBody(sections = uiState.sections)

                    // 연관 크루 - web: CrewMemberSection
                    if (uiState.relatedCrewNames.isNotEmpty()) {
                        LinkedCrewSection(crews = uiState.relatedCrewNames)
                    }
                }
            }
        }

        // 푸터 - web: DocumentFooter
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = "이 문서는 ${formatDateTime(uiState.lastEditedLabel)}에 마지막으로 편집되었습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.c800,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("질문, 제안, 오류 제보는 ")
                            withStyle(
                                SpanStyle(
                                    color = colors.primary.base,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline,
                                ),
                            ) {
                                append("문의하기")
                            }
                            append("를 이용해 주세요.")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.c800,
                    )
                }
            }
        }
    }
}

// web: DocumentHeader - flex justify-between (title 왼쪽, 버튼 오른쪽)
@Composable
private fun DocumentDetailHeader(
    title: String,
    onEditClick: () -> Unit,
    onLogsClick: () -> Unit,
    onWriteClick: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        // DocumentTitle - web: text-2xl font-bold
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.grayscale.c800,
            modifier = Modifier.weight(1f).padding(end = spacing.md),
        )
        // nav 버튼들
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
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
    }
}

// web: TOC component
@Composable
private fun TableOfContentsSection(sections: List<DocumentSectionUiModel>) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    CrewWikiSurfaceSection(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Text(
                text = "목차",
                style = MaterialTheme.typography.titleMedium,
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

// web: OrganizationSection - "소속" 제목 + chip 목록
@Composable
private fun OrganizationSection(
    organizations: List<OrganizationReference>,
    onOrganizationClick: (uuid: String) -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = "소속",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colors.grayscale.c800,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            organizations.forEach { org ->
                CrewWikiTagChip(
                    text = org.title,
                    onClick = { onOrganizationClick(org.organizationDocumentUuid) },
                )
            }
        }
    }
}

// web: document body (toastui-editor-contents)
@Composable
private fun DocumentBody(sections: List<DocumentSectionUiModel>) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xl)) {
        sections.forEach { section ->
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = section.heading,
                    style = when (section.level) {
                        1 -> MaterialTheme.typography.headlineMedium
                        2 -> MaterialTheme.typography.headlineSmall
                        else -> MaterialTheme.typography.titleLarge
                    },
                    fontWeight = FontWeight.Bold,
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

// web: CrewMemberSection - chip 목록 (연관 크루 문서)
@Composable
private fun LinkedCrewSection(crews: List<String>) {
    val spacing = CrewWikiDesignTokens.spacing
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        crews.forEach { crew ->
            CrewWikiTagChip(text = crew)
        }
    }
}

// ── 상태 & 파싱 ───────────────────────────────────────────────────────────────

@Immutable
private data class DocumentDetailScreenState(
    val title: String,
    val organizations: List<OrganizationReference>,
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
private fun rememberDocumentDetailUiState(documentDetail: CrewWikiDocumentDetail): DocumentDetailScreenState {
    return remember(documentDetail) {
        val document = documentDetail.document
        DocumentDetailScreenState(
            title = document.title,
            organizations = document.organizations,
            relatedCrewNames = documentDetail.relatedCrewDocuments.map { it.title },
            lastEditedLabel = document.generateTime,
            sections = parseDocumentSections(document.contents),
        )
    }
}

private fun parseDocumentSections(contents: String): List<DocumentSectionUiModel> {
    val sections = mutableListOf<DocumentSectionUiModel>()
    var currentLevel = 1
    var currentHeading = ""
    val currentParagraphs = mutableListOf<String>()

    fun flush() {
        val paragraphs = currentParagraphs.map(String::trim).filter(String::isNotEmpty)
        if (currentHeading.isNotBlank() || paragraphs.isNotEmpty()) {
            sections += DocumentSectionUiModel(currentLevel, currentHeading, paragraphs)
        }
        currentParagraphs.clear()
    }

    contents.lineSequence().forEach { rawLine ->
        val line = rawLine.trim()
        when {
            line.startsWith("### ") -> { flush(); currentLevel = 3; currentHeading = line.removePrefix("### ").trim() }
            line.startsWith("## ")  -> { flush(); currentLevel = 2; currentHeading = line.removePrefix("## ").trim() }
            line.startsWith("# ")   -> { flush(); currentLevel = 1; currentHeading = line.removePrefix("# ").trim() }
            line.isNotBlank() -> currentParagraphs += line
        }
    }
    flush()

    return sections.ifEmpty {
        listOf(DocumentSectionUiModel(1, "내용", listOf(contents.trim().ifBlank { "내용이 없습니다." })))
    }
}

// "2026-06-22T10:54:00" → "2026년 6월 22일"
private fun formatDateTime(raw: String): String = try {
    val datePart = raw.substringBefore("T")
    val parts = datePart.split("-")
    "${parts[0]}년 ${parts[1].trimStart('0')}월 ${parts[2].trimStart('0')}일"
} catch (_: Exception) {
    raw
}
