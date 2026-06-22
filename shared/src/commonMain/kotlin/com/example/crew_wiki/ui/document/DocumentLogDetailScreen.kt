package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.DocumentLogDetail
import com.example.crew_wiki.ui.common.CrewWikiSurfaceSection
import com.example.crew_wiki.ui.common.ErrorScreen
import com.example.crew_wiki.ui.common.LoadingScreen

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
    val sections = remember(log.contents) { parseDocumentSections(log.contents) }

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
                    // 제목
                    Text(
                        text = log.title,
                        style = MaterialTheme.typography.displayMedium,
                        color = colors.grayscale.c800,
                    )

                    // 메타 정보
                    Text(
                        text = "편집자: ${log.writer}  ·  ${log.generateTime.formatDateTime()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.grayscale.c500,
                    )

                    // 본문
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
            }
        }

        item {
            CrewWikiSurfaceSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Text(
                    text = "이 버전은 ${log.generateTime.formatDateTime()} 에 저장된 스냅샷입니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.grayscale.text,
                )
            }
        }
    }
}

private data class LogSectionUiModel(
    val level: Int,
    val heading: String,
    val paragraphs: List<String>,
)

private fun parseDocumentSections(contents: String): List<LogSectionUiModel> {
    val sections = mutableListOf<LogSectionUiModel>()
    var currentLevel = 1
    var currentHeading = ""
    val currentParagraphs = mutableListOf<String>()

    fun flush() {
        val paragraphs = currentParagraphs.map(String::trim).filter(String::isNotEmpty)
        if (currentHeading.isNotBlank() || paragraphs.isNotEmpty()) {
            sections += LogSectionUiModel(currentLevel, currentHeading, paragraphs.ifEmpty { listOf("내용이 없습니다.") })
        }
        currentParagraphs.clear()
    }

    contents.lineSequence().forEach { raw ->
        val line = raw.trim()
        when {
            line.startsWith("### ") -> { flush(); currentLevel = 3; currentHeading = line.removePrefix("### ") }
            line.startsWith("## ") -> { flush(); currentLevel = 2; currentHeading = line.removePrefix("## ") }
            line.startsWith("# ") -> { flush(); currentLevel = 1; currentHeading = line.removePrefix("# ") }
            line.isNotBlank() -> currentParagraphs += line
        }
    }
    flush()

    return sections.ifEmpty {
        listOf(LogSectionUiModel(1, "본문", listOf(contents)))
    }
}

private fun String.formatDateTime(): String = try {
    val d = substringBefore("T")
    val t = substringAfter("T").substring(0, 5)
    "${d.replace("-", ".")} $t"
} catch (e: Exception) { this }
