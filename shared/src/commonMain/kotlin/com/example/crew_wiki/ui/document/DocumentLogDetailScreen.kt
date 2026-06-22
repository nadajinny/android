package com.example.crew_wiki.ui.document

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.DocumentLogDetail
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.ErrorScreen
import com.example.crew_wiki.ui.common.LoadingScreen
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun DocumentLogDetailScreen(
    viewModel: DocumentLogDetailViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DocumentLogDetailUiState.Loading -> LoadingScreen(modifier)
        is DocumentLogDetailUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = viewModel::loadLog,
            modifier = modifier,
        )
        is DocumentLogDetailUiState.Success -> DocumentLogDetailContent(
            log = state.log,
            modifier = modifier,
        )
    }
}

@Composable
private fun DocumentLogDetailContent(
    log: DocumentLogDetail,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(spacing.xl),
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
                    // 제목
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.grayscale.c800,
                    )

                    // 메타 정보
                    Text(
                        text = "편집자: ${log.writer}  ·  ${log.generateTime.formatLogDateTime()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.c500,
                    )

                    // 본문 마크다운
                    if (log.contents.isNotBlank()) {
                        Markdown(
                            content = log.contents,
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
                                text = MaterialTheme.typography.bodyLarge,
                                code = MaterialTheme.typography.bodyMedium,
                                paragraph = MaterialTheme.typography.bodyLarge,
                            ),
                        )
                    }
                }
            }
        }

        // 스냅샷 푸터
        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "이 버전은 ${log.generateTime.formatLogDateTime()}에 저장된 스냅샷입니다.",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.grayscale.c600,
                )
            }
        }
    }
}

// "2026-06-22T10:54:00" → "2026.06.22 10:54"
private fun String.formatLogDateTime(): String = try {
    val d = substringBefore("T")
    val t = substringAfter("T").substring(0, 5)
    "${d.replace("-", ".")} $t"
} catch (_: Exception) { this }
