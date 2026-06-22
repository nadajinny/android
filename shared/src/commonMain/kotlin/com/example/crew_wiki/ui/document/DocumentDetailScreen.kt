package com.example.crew_wiki.ui.document

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentDetailScreen(
    documentDetail: CrewWikiDocumentDetail,
    onEditClick: () -> Unit = {},
    onLogsClick: () -> Unit = {},
    onWriteClick: () -> Unit = {},
    onOrganizationClick: (uuid: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val document = documentDetail.document
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
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
                    // 헤더
                    DocumentDetailHeader(
                        title = document.title,
                        onEditClick = onEditClick,
                        onLogsClick = onLogsClick,
                        onWriteClick = onWriteClick,
                    )

                    // 소속 섹션
                    if (document.organizations.isNotEmpty()) {
                        OrganizationSection(
                            organizations = document.organizations,
                            onOrganizationClick = onOrganizationClick,
                        )
                    }

                    // 본문 마크다운 — fillMaxWidth() 필수 (LazyColumn 내 무한 width 방지)
                    val content = document.contents.preprocessMarkdown()
                    if (content.isNotBlank()) {
                        Markdown(
                            content = content,
                            modifier = Modifier.fillMaxWidth(),
                            colors = markdownColor(
                                text = colors.grayscale.text,
                                codeText = MaterialTheme.colorScheme.onSurfaceVariant,
                                codeBackground = MaterialTheme.colorScheme.surfaceVariant,
                                linkText = colors.primary.base,
                            ),
                            typography = markdownTypography(
                                h1 = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                h2 = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                h3 = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                h4 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                h5 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                h6 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                text = MaterialTheme.typography.bodyLarge,
                                code = MaterialTheme.typography.bodyMedium,
                                paragraph = MaterialTheme.typography.bodyLarge,
                            ),
                        )
                    }

                    // 연관 크루 문서
                    if (documentDetail.relatedCrewDocuments.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            documentDetail.relatedCrewDocuments.forEach { crew ->
                                CrewWikiTagChip(text = crew.title)
                            }
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
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = "이 문서는 ${formatDocDate(document.generateTime)}에 마지막으로 편집되었습니다.",
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
                            ) { append("문의하기") }
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
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colors.grayscale.c800,
            modifier = Modifier.weight(1f).padding(end = spacing.md),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            CrewWikiActionButton(text = "편집하기", onClick = onEditClick, style = CrewWikiActionButtonStyle.Tertiary)
            CrewWikiActionButton(text = "편집기록", onClick = onLogsClick, style = CrewWikiActionButtonStyle.Tertiary)
            CrewWikiActionButton(text = "작성하기", onClick = onWriteClick, style = CrewWikiActionButtonStyle.Primary)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
            modifier = Modifier.fillMaxWidth(),
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

// "2026-06-22T10:54:00" → "2026년 6월 22일"
internal fun formatDocDate(raw: String): String = try {
    val parts = raw.substringBefore("T").split("-")
    "${parts[0]}년 ${parts[1].trimStart('0')}월 ${parts[2].trimStart('0')}일"
} catch (_: Exception) { raw }

/**
 * iOS Metal 렌더러 크래시 방지:
 * - <br>, <br/> → 빈 줄 (마크다운 단락 구분)
 * - 기타 HTML 인라인 태그 제거
 */
internal fun String.preprocessMarkdown(): String = this
    .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n\n")
    .replace(Regex("<[^>]+>"), "")  // 처리되지 않은 HTML 태그 제거
    .trimEnd()
