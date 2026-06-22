package com.example.crew_wiki.ui.history

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens
import com.example.crew_wiki.model.RecentDocument

@Composable
fun RecentlyViewedScreen(
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
                text = "최근에 확인한 문서가 없습니다.",
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(documents) { doc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDocumentClick(doc) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, colors.primary.c100),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = doc.title,
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
            }
        }
    }
}
