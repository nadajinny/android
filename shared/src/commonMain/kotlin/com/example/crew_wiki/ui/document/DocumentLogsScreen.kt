package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.DocumentLogSummary
import com.example.crew_wiki.ui.common.ErrorScreen
import com.example.crew_wiki.ui.common.LoadingScreen

@Composable
fun DocumentLogsScreen(
    viewModel: DocumentLogsViewModel,
    onLogClick: (logId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DocumentLogsUiState.Loading -> LoadingScreen(modifier)
        is DocumentLogsUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = viewModel::loadLogs,
            modifier = modifier,
        )
        is DocumentLogsUiState.Success -> DocumentLogsContent(
            state = state,
            onLogClick = onLogClick,
            onLoadMore = viewModel::loadNextPage,
            modifier = modifier,
        )
    }
}

@Composable
private fun DocumentLogsContent(
    state: DocumentLogsUiState.Success,
    onLogClick: (logId: Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors
    val listState = rememberLazyListState()

    // 마지막 아이템 도달 시 다음 페이지 로드
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.primary.c50)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "버전",
                modifier = Modifier.weight(0.15f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "생성일시",
                modifier = Modifier.weight(0.4f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "문서 크기",
                modifier = Modifier.weight(0.25f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "편집자",
                modifier = Modifier.weight(0.2f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
        }

        // web: gap-4 flex-col
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            items(state.logs) { log ->
                DocumentLogItem(
                    log = log,
                    onClick = { onLogClick(log.id) },
                )
            }

            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = colors.primary.base)
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// web: LogContent - rounded-2xl border border-primary-100
@Composable
private fun DocumentLogItem(
    log: DocumentLogSummary,
    onClick: () -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, colors.primary.c100),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 버전
            Text(
                text = "${log.version}",
                modifier = Modifier.weight(0.15f),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            // 생성일시
            Text(
                text = log.generateTime.formatDateTime(),
                modifier = Modifier.weight(0.45f),
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            // 문서 크기
            Text(
                text = "${log.documentBytes}B",
                modifier = Modifier.weight(0.2f),
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
            // 편집자
            Text(
                text = log.writer,
                modifier = Modifier.weight(0.2f),
                style = MaterialTheme.typography.bodySmall,
                color = colors.grayscale.c800,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// "2026-06-22T10:54:00" → "2026.06.22 10:54"
private fun String.formatDateTime(): String {
    return try {
        val datePart = substringBefore("T")
        val timePart = substringAfter("T").substring(0, 5)
        "${datePart.replace("-", ".")} $timePart"
    } catch (e: Exception) {
        this
    }
}

// 바이트 → KB 표시
private fun Long.toReadableSize(): String {
    if (this < 1024) return "${this}B"
    val kb = this / 1024
    val remainder = (this % 1024) / 103  // ≈ 0.1 단위
    return "${kb}.${remainder}KB"
}
