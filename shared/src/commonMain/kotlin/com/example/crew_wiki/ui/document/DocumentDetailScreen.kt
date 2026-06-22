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
        // 액션 버튼 (편집하기 / 편집기록 / 작성하기) - 텍스트 박스 바깥, 최상단
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    CrewWikiActionButton(text = "편집하기", onClick = onEditClick, style = CrewWikiActionButtonStyle.Tertiary)
                    CrewWikiActionButton(text = "편집기록", onClick = onLogsClick, style = CrewWikiActionButtonStyle.Tertiary)
                    CrewWikiActionButton(text = "작성하기", onClick = onWriteClick, style = CrewWikiActionButtonStyle.Primary)
                }
            }
        }

        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.xl),
                ) {
                    // 제목
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.grayscale.c800,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // 소속 섹션
                    if (document.organizations.isNotEmpty()) {
                        OrganizationSection(
                            organizations = document.organizations,
                            onOrganizationClick = onOrganizationClick,
                        )
                    }

                    // 본문 마크다운 (자체 렌더러)
                    val content = document.contents.preprocessMarkdown()
                    if (content.isNotBlank()) {
                        MarkdownContent(
                            content = content,
                            modifier = Modifier.fillMaxWidth(),
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

internal fun formatDocDate(raw: String): String = try {
    val parts = raw.substringBefore("T").split("-")
    "${parts[0]}년 ${parts[1].trimStart('0')}월 ${parts[2].trimStart('0')}일"
} catch (_: Exception) { raw }
