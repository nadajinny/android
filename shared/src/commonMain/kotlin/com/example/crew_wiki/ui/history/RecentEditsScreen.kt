package com.example.crew_wiki.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.RecentDocument

@Composable
fun RecentEditsScreen(
    documents: List<RecentDocument>,
    onDocumentClick: (RecentDocument) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors

    if (documents.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "편집된 문서가 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.grayscale.c500,
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        items(documents) { doc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDocumentClick(doc) }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "[${doc.generateTime.formatRecentEditDate()}] ${doc.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.grayscale.c800,
                    modifier = Modifier.weight(1f),
                )
                if (doc.documentType == "ORGANIZATION") {
                    Text(
                        text = "그룹",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.primary.c600,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            HorizontalDivider(color = colors.grayscale.c100)
        }
    }
}

private fun String.formatRecentEditDate(): String = try {
    substringBefore("T").replace("-", ".")
} catch (_: Exception) {
    this
}
