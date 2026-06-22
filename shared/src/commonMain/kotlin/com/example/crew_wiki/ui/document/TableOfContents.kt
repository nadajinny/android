package com.example.crew_wiki.ui.document

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crew_wiki.CrewWikiDesignTokens

private const val MAX_TOC_LEVEL = 3

private data class TocEntry(
    val headingIndex: Int,
    val level: Int,
    val text: String,
    val number: String,
)

private val TOC_LEVEL_INDENT: Map<Int, Int> = mapOf(1 to 0, 2 to 15, 3 to 30)

/**
 * 본문의 h1~h3 헤딩으로 목차를 구성한다. (web의 TOC.tsx와 동일한 번호 매기기 규칙)
 * [headings]는 [extractMarkdownHeadings]의 결과이며, 인덱스가 [MarkdownContent]의
 * headingRequesters 인덱스와 1:1로 대응한다.
 */
@Composable
fun TableOfContents(
    headings: List<MarkdownHeadingOutline>,
    onEntryClick: (headingIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
) {
    val colors = CrewWikiDesignTokens.colors
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    val entries = remember(headings) { buildTocEntries(headings) }
    if (entries.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, colors.grayscale.c100),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "목차",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.grayscale.c800,
                )
                Text(
                    text = if (expanded) "▴" else "▾",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.grayscale.c600,
                )
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp)),
                ) {
                    entries.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEntryClick(entry.headingIndex) }
                                .padding(
                                    start = (TOC_LEVEL_INDENT[entry.level] ?: 0).dp,
                                    top = 4.dp,
                                    bottom = 4.dp,
                                ),
                        ) {
                            Text(
                                text = entry.number,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.primary.base,
                            )
                            Text(
                                text = " ${entry.text}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.grayscale.c800,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildTocEntries(headings: List<MarkdownHeadingOutline>): List<TocEntry> {
    val counts = intArrayOf(0, 0, 0)
    val entries = mutableListOf<TocEntry>()

    headings.forEachIndexed { headingIndex, heading ->
        val level = heading.level
        if (level !in 1..MAX_TOC_LEVEL) return@forEachIndexed

        for (idx in counts.indices) {
            counts[idx] = when {
                idx < level - 1 -> counts[idx]
                idx == level - 1 -> counts[idx] + 1
                else -> 0
            }
        }
        val number = counts.take(level).joinToString(".")
        entries += TocEntry(headingIndex = headingIndex, level = level, text = heading.text, number = number)
    }

    return entries
}
