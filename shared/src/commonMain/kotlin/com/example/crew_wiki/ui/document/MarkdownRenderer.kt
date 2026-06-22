package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.crew_wiki.CrewWikiDesignTokens

/** 목차(TOC) 항목 — h1~h6 중 본문에 등장한 순서대로, [MarkdownContent]의 headingRequesters 인덱스와 1:1 대응한다. */
data class MarkdownHeadingOutline(val level: Int, val text: String)

/** [content]에서 헤딩 목록을 추출한다. [MarkdownContent]가 그리는 헤딩과 동일한 순서/개수를 보장한다. */
fun extractMarkdownHeadings(content: String): List<MarkdownHeadingOutline> =
    parseMarkdownBlocks(content).filterIsInstance<MarkdownBlock.Heading>().map {
        MarkdownHeadingOutline(level = it.level, text = it.text)
    }

/**
 * KMP iOS 안전 마크다운 렌더러 (자체 구현)
 * 지원: h1~h6, 단락, 표, **bold**, *italic*, `code`, 이미지, 수평선, 순서/비순서 목록, <br>
 */
@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier,
    headingRequesters: List<BringIntoViewRequester> = emptyList(),
) {
    val blocks = parseMarkdownBlocks(content)
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing
    val uriHandler = LocalUriHandler.current
    var headingCounter = 0

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    val headingIndex = headingCounter++
                    MarkdownHeading(
                        text = block.text,
                        level = block.level,
                        isFirstBlock = index == 0,
                        modifier = headingRequesters.getOrNull(headingIndex)?.let {
                            Modifier.bringIntoViewRequester(it)
                        } ?: Modifier,
                    )
                }

                is MarkdownBlock.Paragraph -> {
                    if (index > 0) Spacer(Modifier.height(spacing.sm))
                    Text(
                        text = block.annotated,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.grayscale.text,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                is MarkdownBlock.BlockQuote -> {
                    Spacer(Modifier.height(14.dp))
                    MarkdownBlockQuote(block.annotated)
                    Spacer(Modifier.height(14.dp))
                }

                is MarkdownBlock.Table -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    MarkdownTable(table = block)
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.HtmlTable -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    MarkdownHtmlTable(table = block)
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.Image -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    MarkdownImageBlock(
                        block = block,
                        uriHandlerOpen = uriHandler::openUri,
                    )
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.Code -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                            .padding(12.dp),
                    ) {
                        Text(
                            text = block.code,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.HorizontalRule -> {
                    Spacer(Modifier.height(spacing.md))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.ListItem -> {
                    if (index == 0 || (blocks[index - 1] !is MarkdownBlock.ListItem && blocks[index - 1] !is MarkdownBlock.ListImage)) {
                        Spacer(Modifier.height(spacing.sm))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = block.indentLevel.indentPadding()),
                    ) {
                        Text(
                            text = if (block.ordered) "${block.order}." else "•",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.grayscale.c600,
                            modifier = Modifier.width(24.dp),
                        )
                        Text(
                            text = block.annotated,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.grayscale.text,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                is MarkdownBlock.ListImage -> {
                    if (index == 0 || (blocks[index - 1] !is MarkdownBlock.ListItem && blocks[index - 1] !is MarkdownBlock.ListImage)) {
                        Spacer(Modifier.height(spacing.sm))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = block.indentLevel.indentPadding()),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = if (block.ordered) "${block.order}." else "•",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.grayscale.c600,
                            modifier = Modifier.width(24.dp),
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            MarkdownImageBlock(
                                block = block.image,
                                uriHandlerOpen = uriHandler::openUri,
                            )
                        }
                    }
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.Blank -> {
                    Spacer(Modifier.height(spacing.xs))
                }
            }
        }
    }
}

@Composable
private fun MarkdownImageBlock(
    block: MarkdownBlock.Image,
    uriHandlerOpen: (String) -> Unit,
) {
    val colors = CrewWikiDesignTokens.colors
    val imageCaption = block.alt.toVisibleImageCaption()
    AsyncImage(
        model = block.url,
        contentDescription = imageCaption,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .then(
                if (block.linkUrl != null) {
                    Modifier.clickable { uriHandlerOpen(block.linkUrl) }
                } else {
                    Modifier
                },
            ),
    )
    if (imageCaption != null) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = imageCaption,
            style = MaterialTheme.typography.labelSmall,
            color = colors.grayscale.c500,
        )
    }
}

@Composable
private fun MarkdownHeading(
    text: String,
    level: Int,
    isFirstBlock: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = CrewWikiDesignTokens.colors

    Column(modifier = modifier) {
    when (level) {
        1 -> {
            Spacer(Modifier.height(if (isFirstBlock) 14.dp else 52.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.grayscale.c800,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(7.dp))
            DoubleHorizontalDivider(
                primaryColor = Color(0xFF999999),
                secondaryColor = Color(0xFF999999),
            )
            Spacer(Modifier.height(15.dp))
        }

        2 -> {
            Spacer(Modifier.height(20.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.grayscale.c800,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(7.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFDBDBDB),
            )
            Spacer(Modifier.height(13.dp))
        }

        3 -> {
            Spacer(Modifier.height(18.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.grayscale.c800,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(2.dp))
        }

        4 -> {
            Spacer(Modifier.height(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.grayscale.c800,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(2.dp))
        }

        else -> {
            Spacer(Modifier.height(9.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.grayscale.c800,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
        }
    }
    }
}

@Composable
private fun DoubleHorizontalDivider(
    primaryColor: Color,
    secondaryColor: Color,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = primaryColor,
        )
        Spacer(Modifier.height(1.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = secondaryColor,
        )
    }
}

@Composable
private fun MarkdownBlockQuote(
    text: AnnotatedString,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(Color(0xFFE5E5E5)),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF999999),
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        )
    }
}

// ── 표 렌더러 ──────────────────────────────────────────────────────────────────

private val TableBorderColor = Color(0x1A000000)
private val TableLabelBackground = Color(0xFF555555)
private val TableLabelTextColor = Color.White
private val TableValueBackground = Color.White
private val TableValueTextColor = Color(0xFF222222)
private val TableCellMinWidth = 96.dp

@Composable
private fun MarkdownTable(table: MarkdownBlock.Table) {
    val colCount = maxOf(table.headers.size, table.rows.maxOfOrNull { it.size } ?: 0)
    if (colCount == 0) return

    val allRows = listOf(table.headers.normalizeTableRow(colCount)) +
        table.rows.map { it.normalizeTableRow(colCount) }
    val rowSpans = remember(allRows) { allRows.map { row -> List(row.size) { 1 } } }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(TableValueBackground)
            .border(1.dp, TableBorderColor),
    ) {
        val availableWidthPx = constraints.maxWidth
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            TableGridLayout(
                rowSpans = rowSpans,
                totalColumns = colCount,
                minTotalWidthPx = availableWidthPx,
            ) {
                allRows.forEach { row ->
                    row.forEachIndexed { colIdx, cell ->
                        TableCell(
                            text = parseInline(cell.trim()),
                            isLabelColumn = colIdx == 0,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownHtmlTable(table: MarkdownBlock.HtmlTable) {
    val totalColumns = table.rows.maxOfOrNull { row -> row.cells.sumOf { it.colspan } } ?: 0
    if (totalColumns == 0) return

    val rowSpans = remember(table.rows) { table.rows.map { row -> row.cells.map { it.colspan } } }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(TableValueBackground)
            .border(1.dp, TableBorderColor),
    ) {
        val availableWidthPx = constraints.maxWidth
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            TableGridLayout(
                rowSpans = rowSpans,
                totalColumns = totalColumns,
                minTotalWidthPx = availableWidthPx,
            ) {
                table.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        HtmlTableCell(cell = cell)
                    }
                }
            }
        }
    }
}

/**
 * 모든 행의 동일한 열이 같은 너비를, 같은 행의 셀들이 같은 높이를 갖도록
 * 2-pass로 측정/배치하는 표 그리드. colspan을 지원한다.
 */
@Composable
private fun TableGridLayout(
    rowSpans: List<List<Int>>,
    totalColumns: Int,
    minTotalWidthPx: Int,
    modifier: Modifier = Modifier,
    cellMinWidth: Dp = TableCellMinWidth,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { _ ->
        val minWidthPx = cellMinWidth.roundToPx()

        // 1차: 제약 없이 측정하여 각 셀의 자연스러운 크기를 파악한다.
        val naturalPlaceables = subcompose(0, content).map { it.measure(Constraints()) }

        val colWidths = IntArray(totalColumns) { minWidthPx }
        val rowHeights = IntArray(rowSpans.size)
        var idx = 0
        for ((rowIndex, row) in rowSpans.withIndex()) {
            var col = 0
            for (span in row) {
                val placeable = naturalPlaceables[idx]
                val perColWidth = (placeable.width + span - 1) / span
                for (c in col until (col + span).coerceAtMost(totalColumns)) {
                    colWidths[c] = maxOf(colWidths[c], perColWidth)
                }
                rowHeights[rowIndex] = maxOf(rowHeights[rowIndex], placeable.height)
                col += span
                idx++
            }
        }

        // 표가 화면보다 좁으면 남는 너비를 열에 균등 분배해 꽉 채운다.
        val naturalTotal = colWidths.sum()
        if (minTotalWidthPx > naturalTotal) {
            val extra = minTotalWidthPx - naturalTotal
            val per = extra / totalColumns
            val remainder = extra % totalColumns
            for (i in colWidths.indices) {
                colWidths[i] += per + if (i < remainder) 1 else 0
            }
        }

        // 2차: 확정된 열 너비·행 높이로 모든 셀을 동일하게 고정 측정한다.
        val finalPlaceables = subcompose(1, content)
        val placeables = arrayOfNulls<Placeable>(finalPlaceables.size)
        idx = 0
        for ((rowIndex, row) in rowSpans.withIndex()) {
            var col = 0
            for (span in row) {
                val cellWidth = (col until (col + span).coerceAtMost(totalColumns)).sumOf { colWidths[it] }
                placeables[idx] = finalPlaceables[idx].measure(
                    Constraints.fixed(cellWidth, rowHeights[rowIndex]),
                )
                col += span
                idx++
            }
        }

        layout(colWidths.sum(), rowHeights.sum()) {
            var y = 0
            idx = 0
            for ((rowIndex, row) in rowSpans.withIndex()) {
                var x = 0
                var col = 0
                for (span in row) {
                    placeables[idx]?.placeRelative(x, y)
                    val cellWidth = (col until (col + span).coerceAtMost(totalColumns)).sumOf { colWidths[it] }
                    x += cellWidth
                    col += span
                    idx++
                }
                y += rowHeights[rowIndex]
            }
        }
    }
}

@Composable
private fun TableCell(
    text: AnnotatedString,
    isLabelColumn: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 72.dp)
            .background(if (isLabelColumn) TableLabelBackground else TableValueBackground)
            .border(width = 0.5.dp, color = TableBorderColor),
        contentAlignment = if (isLabelColumn) Alignment.Center else Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isLabelColumn) FontWeight.SemiBold else FontWeight.Bold,
            ),
            color = if (isLabelColumn) TableLabelTextColor else TableValueTextColor,
            textAlign = if (isLabelColumn) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
        )
    }
}

private fun List<String>.normalizeTableRow(columnCount: Int): List<String> =
    this + List((columnCount - size).coerceAtLeast(0)) { "" }

@Composable
private fun HtmlTableCell(
    cell: MarkdownBlock.HtmlTableCell,
    modifier: Modifier = Modifier,
) {
    val isImageCell = cell.imageUrl != null
    val backgroundColor = when {
        isImageCell -> TableValueBackground
        cell.isHeader -> TableLabelBackground
        else -> TableValueBackground
    }
    val contentAlignment = when {
        isImageCell -> Alignment.Center
        cell.align == TableAlign.Center || cell.isHeader -> Alignment.Center
        cell.align == TableAlign.End -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = if (isImageCell) 220.dp else 72.dp)
            .background(backgroundColor)
            .border(width = 0.5.dp, color = TableBorderColor),
        contentAlignment = contentAlignment,
    ) {
        when {
            cell.imageUrl != null -> {
                AsyncImage(
                    model = cell.imageUrl,
                    contentDescription = cell.text.ifBlank { null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }

            else -> {
                Text(
                    text = parseInline(cell.text),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (cell.isHeader) FontWeight.SemiBold else FontWeight.Bold,
                    ),
                    color = if (cell.isHeader) TableLabelTextColor else TableValueTextColor,
                    textAlign = when {
                        cell.align == TableAlign.Center || cell.isHeader -> TextAlign.Center
                        cell.align == TableAlign.End -> TextAlign.End
                        else -> TextAlign.Start
                    },
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                )
            }
        }
    }
}

// ── 블록 타입 ──────────────────────────────────────────────────────────────────

private enum class TableAlign { Start, Center, End }

private sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val annotated: AnnotatedString) : MarkdownBlock
    data class BlockQuote(val annotated: AnnotatedString) : MarkdownBlock
    data class Table(
        val headers: List<String>,
        val alignments: List<TableAlign>,
        val rows: List<List<String>>,
    ) : MarkdownBlock
    data class HtmlTable(
        val rows: List<HtmlTableRow>,
    ) : MarkdownBlock
    data class HtmlTableRow(
        val cells: List<HtmlTableCell>,
    )
    data class HtmlTableCell(
        val text: String,
        val imageUrl: String?,
        val isHeader: Boolean,
        val colspan: Int,
        val align: TableAlign,
    )
    data class Image(val alt: String, val url: String, val linkUrl: String? = null) : MarkdownBlock
    data class Code(val language: String, val code: String) : MarkdownBlock
    data object HorizontalRule : MarkdownBlock
    data class ListItem(
        val ordered: Boolean,
        val order: Int,
        val annotated: AnnotatedString,
        val indentLevel: Int,
    ) : MarkdownBlock
    data class ListImage(
        val ordered: Boolean,
        val order: Int,
        val image: Image,
        val indentLevel: Int,
    ) : MarkdownBlock
    data object Blank : MarkdownBlock
}

// ── 파서 ───────────────────────────────────────────────────────────────────────

private val headingRegex = Regex("^(#{1,6})\\s+(.*)")
private val blockQuoteRegex = Regex("^(\\s*)>\\s?(.*)$")
private val hrRegex = Regex("^[-*_]{3,}\\s*$")
private val orderedListRegex = Regex("^(\\s*)(\\d+)\\.\\s*(.*)")
private val unorderedListRegex = Regex("^(\\s*)[-*+]\\s*(.*)")
private val fenceStart = Regex("^```(\\w*)")
private val tableRowRegex = Regex("^\\|(.+)\\|\\s*$")
private val tableSepRegex = Regex("^\\|[-:| ]+\\|\\s*$")
private val htmlTableRowRegex = Regex("<tr\\b[^>]*>([\\s\\S]*?)</tr>", RegexOption.IGNORE_CASE)
private val htmlTableCellRegex = Regex("<(th|td)\\b([^>]*)>([\\s\\S]*?)</(?:th|td)>", RegexOption.IGNORE_CASE)
private val tableTagStartRegex = Regex("<table\\b", RegexOption.IGNORE_CASE)
private val tableTagEndRegex = Regex("</table>", RegexOption.IGNORE_CASE)
private val nonTableHtmlRegex = Regex("</?(?!table\\b|thead\\b|tbody\\b|tr\\b|th\\b|td\\b|img\\b)[A-Za-z][^>]*>", RegexOption.IGNORE_CASE)
private val htmlColspanRegex = Regex("""colspan\s*=\s*"(\d+)"""", RegexOption.IGNORE_CASE)
private val htmlAlignRegex = Regex("""align\s*=\s*"([^"]+)"""", RegexOption.IGNORE_CASE)
private val htmlImageSrcRegex = Regex("""<img\b[^>]*src\s*=\s*"([^"]+)"""", RegexOption.IGNORE_CASE)

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val text = raw.preprocessMarkdown()

    val blocks = mutableListOf<MarkdownBlock>()
    val lines = text.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // 코드 블록
        if (fenceStart.containsMatchIn(line)) {
            val lang = fenceStart.find(line)!!.groupValues[1]
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].startsWith("```")) {
                codeLines += lines[i]; i++
            }
            blocks += MarkdownBlock.Code(lang, codeLines.joinToString("\n"))
            i++; continue
        }

        if (tableTagStartRegex.containsMatchIn(line)) {
            val tableLines = mutableListOf(line)
            while (i + 1 < lines.size && !tableTagEndRegex.containsMatchIn(tableLines.last())) {
                i++
                tableLines += lines[i]
            }
            parseHtmlTableBlock(tableLines.joinToString("\n"))?.let { tableBlock ->
                blocks += tableBlock
                i++
                continue
            }
        }

        // 표: 헤더 행 | 구분 행 | 데이터 행
        if (tableRowRegex.matches(line) && i + 1 < lines.size && tableSepRegex.matches(lines[i + 1])) {
            val headers = splitTableRow(line)
            val alignments = parseTableAlignments(lines[i + 1])
            val rows = mutableListOf<List<String>>()
            i += 2
            while (i < lines.size && tableRowRegex.matches(lines[i])) {
                rows += splitTableRow(lines[i]); i++
            }
            blocks += MarkdownBlock.Table(headers, alignments, rows)
            continue
        }

        // 이미지 / 링크가 걸린 이미지
        parseImageBlock(line.trim())?.let { imageBlock ->
            blocks += imageBlock
            i++; continue
        }

        // 인용문
        val blockQuoteMatch = blockQuoteRegex.find(line)
        if (blockQuoteMatch != null) {
            val quoteLines = mutableListOf(blockQuoteMatch.groupValues[2])
            while (i + 1 < lines.size) {
                val next = lines[i + 1]
                val nextMatch = blockQuoteRegex.find(next) ?: break
                quoteLines += nextMatch.groupValues[2]
                i++
            }
            blocks += MarkdownBlock.BlockQuote(parseInline(quoteLines.joinToString("\n")))
            i++; continue
        }

        // 수평선
        if (hrRegex.matches(line)) {
            blocks += MarkdownBlock.HorizontalRule; i++; continue
        }

        // 제목
        val headingMatch = headingRegex.find(line)
        if (headingMatch != null) {
            blocks += MarkdownBlock.Heading(
                level = headingMatch.groupValues[1].length,
                text = headingMatch.groupValues[2].trim(),
            )
            i++; continue
        }

        // 순서 있는 목록
        val olMatch = orderedListRegex.find(line)
        if (olMatch != null) {
            val indentLevel = olMatch.groupValues[1].length / 4
            val content = olMatch.groupValues[3].trim()
            val imageBlock = parseImageBlock(content)
            blocks += if (imageBlock != null) {
                MarkdownBlock.ListImage(
                    ordered = true,
                    order = olMatch.groupValues[2].toIntOrNull() ?: 1,
                    image = imageBlock,
                    indentLevel = indentLevel,
                )
            } else {
                MarkdownBlock.ListItem(
                    ordered = true,
                    order = olMatch.groupValues[2].toIntOrNull() ?: 1,
                    annotated = parseInline(content),
                    indentLevel = indentLevel,
                )
            }
            i++; continue
        }

        // 순서 없는 목록
        val ulMatch = unorderedListRegex.find(line)
        if (ulMatch != null) {
            val indentLevel = ulMatch.groupValues[1].length / 4
            val content = ulMatch.groupValues[2].trim()
            val imageBlock = parseImageBlock(content)
            blocks += if (imageBlock != null) {
                MarkdownBlock.ListImage(
                    ordered = false,
                    order = 0,
                    image = imageBlock,
                    indentLevel = indentLevel,
                )
            } else {
                MarkdownBlock.ListItem(
                    ordered = false, order = 0,
                    annotated = parseInline(content),
                    indentLevel = indentLevel,
                )
            }
            i++; continue
        }

        // 빈 줄
        if (line.isBlank()) {
            if (blocks.lastOrNull() !is MarkdownBlock.Blank) blocks += MarkdownBlock.Blank
            i++; continue
        }

        // 일반 단락
        val paragraphLines = mutableListOf(line)
        while (i + 1 < lines.size) {
            val next = lines[i + 1]
            if (next.isBlank() || headingRegex.containsMatchIn(next) ||
                hrRegex.matches(next) || fenceStart.containsMatchIn(next) ||
                tableRowRegex.matches(next) || blockQuoteRegex.matches(next) ||
                orderedListRegex.matches(next) || unorderedListRegex.matches(next) ||
                parseImageBlock(next.trim()) != null
            ) break
            paragraphLines += next; i++
        }
        blocks += MarkdownBlock.Paragraph(parseInline(paragraphLines.joinToString(" ")))
        i++
    }

    return blocks
}

/** `| a | b | c |` → `["a", "b", "c"]` */
private fun splitTableRow(line: String): List<String> =
    line.trim().removePrefix("|").removeSuffix("|").split("|")

/** `|:---|:---:|---:|` → [Start, Center, End, ...] */
private fun parseTableAlignments(line: String): List<TableAlign> =
    splitTableRow(line).map { cell ->
        val t = cell.trim()
        when {
            t.startsWith(":") && t.endsWith(":") -> TableAlign.Center
            t.endsWith(":") -> TableAlign.End
            else -> TableAlign.Start
        }
    }

// ── 인라인 파서 (**bold**, *italic*, `code`, [link](url)) ────────────────────

private fun parseInline(text: String): AnnotatedString = buildAnnotatedString {
    var pos = 0
    while (pos < text.length) {
        when {
            pos + 1 < text.length && text.startsWith("~~", pos) -> {
                val end = text.indexOf("~~", pos + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append(text.substring(pos + 2, end)); pop(); pos = end + 2
                } else { append(text[pos]); pos++ }
            }
            pos + 1 < text.length && text[pos] == '\\' -> {
                append(text[pos + 1])
                pos += 2
            }
            pos + 1 < text.length && (text.startsWith("**", pos) || text.startsWith("__", pos)) -> {
                val marker = text.substring(pos, pos + 2)
                val end = text.indexOf(marker, pos + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(text.substring(pos + 2, end)); pop(); pos = end + 2
                } else { append(text[pos]); pos++ }
            }
            text[pos] == '*' || text[pos] == '_' -> {
                val marker = text[pos].toString()
                val end = text.indexOf(marker, pos + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(text.substring(pos + 1, end)); pop(); pos = end + 1
                } else { append(text[pos]); pos++ }
            }
            text[pos] == '`' -> {
                val end = text.indexOf('`', pos + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x1A000000)))
                    append(text.substring(pos + 1, end)); pop(); pos = end + 1
                } else { append(text[pos]); pos++ }
            }
            text[pos] == '[' -> {
                val closeBracket = text.indexOf(']', pos + 1)
                val openParen = if (closeBracket != -1) text.indexOf('(', closeBracket) else -1
                val closeParen = if (openParen == closeBracket + 1) text.indexOf(')', openParen + 1) else -1
                if (closeParen != -1) {
                    val linkText = text.substring(pos + 1, closeBracket)
                    val url = text.substring(openParen + 1, closeParen)
                    withLink(
                        LinkAnnotation.Url(
                            url = url,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = Color(0xFF1A73E8),
                                    textDecoration = TextDecoration.Underline,
                                ),
                            ),
                        ),
                    ) {
                        append(linkText)
                    }
                    pos = closeParen + 1
                } else { append(text[pos]); pos++ }
            }
            else -> { append(text[pos]); pos++ }
        }
    }
}

/** HTML 전처리: 표 태그는 유지하고, <br> → 줄바꿈 및 기타 HTML 태그만 제거 */
internal fun String.preprocessMarkdown(): String = this
    .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
    .replace(nonTableHtmlRegex, "")
    .trimEnd()

private fun parseHtmlTableBlock(tableHtml: String): MarkdownBlock.HtmlTable? {
    val rows = htmlTableRowRegex.findAll(tableHtml)
        .map { rowMatch ->
            htmlTableCellRegex.findAll(rowMatch.groupValues[1])
                .map { cellMatch ->
                    val tagName = cellMatch.groupValues[1]
                    val attributes = cellMatch.groupValues[2]
                    val cellHtml = cellMatch.groupValues[3]
                    MarkdownBlock.HtmlTableCell(
                        text = cellHtml.stripHtmlCellContent(),
                        imageUrl = htmlImageSrcRegex.find(cellHtml)?.groupValues?.get(1),
                        isHeader = tagName.equals("th", ignoreCase = true),
                        colspan = htmlColspanRegex.find(attributes)?.groupValues?.get(1)?.toIntOrNull() ?: 1,
                        align = htmlAlignRegex.find(attributes)?.groupValues?.get(1).toTableAlign(),
                    )
                }
                .toList()
            }
            .filter { it.isNotEmpty() }
            .toList()

    if (rows.isEmpty()) return null
    return MarkdownBlock.HtmlTable(rows = rows.map { MarkdownBlock.HtmlTableRow(it) })
}

private fun String.stripHtmlCellContent(): String = this
    .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
    .replace(Regex("<[^>]+>"), "")
    .replace("&nbsp;", " ")
    .trim()

private fun String?.toTableAlign(): TableAlign = when (this?.lowercase()) {
    "center" -> TableAlign.Center
    "right", "end" -> TableAlign.End
    else -> TableAlign.Start
}

private fun String.toVisibleImageCaption(): String? {
    val normalized = trim()
    if (normalized.isBlank()) return null
    if (normalized.equals("image", ignoreCase = true)) return null
    return normalized
}

private fun Int.indentPadding() = (this * 20).dp

private fun parseImageBlock(line: String): MarkdownBlock.Image? {
    if (line.startsWith("![")) {
        val image = parseMarkdownImage(line) ?: return null
        if (image.nextIndex != line.length) return null
        return MarkdownBlock.Image(
            alt = image.alt,
            url = image.url,
        )
    }

    if (line.startsWith("[![")) {
        val linkCloseBracket = findMatchingBracket(line, 0) ?: return null
        val linkCloseParen = if (linkCloseBracket + 1 < line.length && line[linkCloseBracket + 1] == '(') {
            findMatchingParen(line, linkCloseBracket + 1)
        } else {
            null
        } ?: return null

        val inner = line.substring(1, linkCloseBracket)
        val image = parseMarkdownImage(inner) ?: return null
        if (image.nextIndex != inner.length) return null

        return MarkdownBlock.Image(
            alt = image.alt,
            url = image.url,
            linkUrl = line.substring(linkCloseBracket + 2, linkCloseParen),
        )
    }

    return null
}

private data class ParsedMarkdownImage(
    val alt: String,
    val url: String,
    val nextIndex: Int,
)

private fun parseMarkdownImage(text: String, startIndex: Int = 0): ParsedMarkdownImage? {
    if (!text.startsWith("![", startIndex)) return null

    val closeBracket = findMatchingBracket(text, startIndex + 1) ?: return null
    if (closeBracket + 1 >= text.length || text[closeBracket + 1] != '(') return null

    val closeParen = findMatchingParen(text, closeBracket + 1) ?: return null
    return ParsedMarkdownImage(
        alt = text.substring(startIndex + 2, closeBracket),
        url = text.substring(closeBracket + 2, closeParen),
        nextIndex = closeParen + 1,
    )
}

private fun findMatchingBracket(text: String, openIndex: Int): Int? {
    if (openIndex !in text.indices || text[openIndex] != '[') return null
    var depth = 0
    for (index in openIndex until text.length) {
        when (text[index]) {
            '[' -> depth++
            ']' -> {
                depth--
                if (depth == 0) return index
            }
        }
    }
    return null
}

private fun findMatchingParen(text: String, openIndex: Int): Int? {
    if (openIndex !in text.indices || text[openIndex] != '(') return null
    var depth = 0
    for (index in openIndex until text.length) {
        when (text[index]) {
            '(' -> depth++
            ')' -> {
                depth--
                if (depth == 0) return index
            }
        }
    }
    return null
}
