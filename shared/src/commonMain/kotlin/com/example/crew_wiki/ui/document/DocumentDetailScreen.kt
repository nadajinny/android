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
import com.example.crew_wiki.model.CrewWikiDocument
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.ui.common.CrewWikiActionButton
import com.example.crew_wiki.ui.common.CrewWikiActionButtonStyle
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.CrewWikiTagChip

@Composable
fun DocumentDetailScreen(
    documentId: String,
    modifier: Modifier = Modifier,
) {
    val uiState = rememberDocumentDetailUiState(documentId)
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
private fun rememberDocumentDetailUiState(documentId: String): DocumentDetailUiState {
    return remember(documentId) {
        val document = CrewWikiDocument(
            documentId = 1,
            documentUUID = documentId,
            title = "크루위키 문서: $documentId",
            contents = """
                ## 기본 정보
                이 영역은 웹의 문서 상세 화면 레이아웃을 Android KMP로 옮기기 위한 스켈레톤입니다.
                실제 API가 연결되면 문서 제목, 소속, 소개, 링크 정보 같은 기본 메타데이터가 여기에 들어오게 됩니다.

                ## 활동과 특징
                본문은 마크다운 렌더링 전략이 정해지기 전까지는 단락 단위의 더미 텍스트로 유지합니다.
                현재 단계에서는 상단 액션, 목차, 크루 칩, 본문 섹션, 하단 메타 푸터가 실제 화면 배치로 잡혀 있는지가 더 중요합니다.

                ### 다음 구현 우선순위
                1순위는 실제 Document 모델 연결, 2순위는 TOC 클릭 이동, 3순위는 마크다운 본문 렌더링입니다.
            """.trimIndent(),
            writer = "비모",
            generateTime = "2026-06-22T10:54:00",
            organizations = listOf(
                OrganizationReference(title = "우테코", uuid = "organization-1"),
                OrganizationReference(title = "백엔드", uuid = "organization-2"),
            ),
        )

        DocumentDetailUiState(
            title = document.title,
            relatedCrewNames = listOf("비모", "우디", "세인", "주디"),
            lastEditedLabel = "2026년 6월 22일 (월) 10:54",
            sections = listOf(
                DocumentSectionUiModel(
                    level = 1,
                    heading = "기본 정보",
                    paragraphs = listOf(
                        "이 영역은 웹의 문서 상세 화면 레이아웃을 Android KMP로 옮기기 위한 스켈레톤입니다.",
                        "실제 API가 연결되면 문서 제목, 소속, 소개, 링크 정보 같은 기본 메타데이터가 여기에 들어오게 됩니다.",
                    ),
                ),
                DocumentSectionUiModel(
                    level = 1,
                    heading = "활동과 특징",
                    paragraphs = listOf(
                        "본문은 마크다운 렌더링 전략이 정해지기 전까지는 단락 단위의 더미 텍스트로 유지합니다.",
                        "현재 단계에서는 상단 액션, 목차, 크루 칩, 본문 섹션, 하단 메타 푸터가 실제 화면 배치로 잡혀 있는지가 더 중요합니다.",
                    ),
                ),
                DocumentSectionUiModel(
                    level = 2,
                    heading = "다음 구현 우선순위",
                    paragraphs = listOf(
                        "1순위는 실제 Document 모델 연결, 2순위는 TOC 클릭 이동, 3순위는 마크다운 본문 렌더링입니다.",
                    ),
                ),
            ),
        )
    }
}
