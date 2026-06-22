package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.crew_wiki.CrewWikiDesignTokens
import kotlin.math.max

/**
 * KMP iOS 안전 마크다운 렌더러 (자체 구현)
 * 지원: h1~h6, 단락, 표, **bold**, *italic*, `code`, 이미지, 수평선, 순서/비순서 목록, <br>
 */
@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier,
) {
    val blocks = parseMarkdownBlocks(content)
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    MarkdownHeading(
                        text = block.text,
                        level = block.level,
                        isFirstBlock = index == 0,
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

                is MarkdownBlock.Table -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    MarkdownTable(table = block)
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.Image -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    val imageCaption = block.alt.toVisibleImageCaption()
                    AsyncImage(
                        model = block.url,
                        contentDescription = imageCaption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .then(
                                if (block.linkUrl != null) {
                                    Modifier.clickable { uriHandler.openUri(block.linkUrl) }
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
                    if (index == 0 || blocks[index - 1] !is MarkdownBlock.ListItem) {
                        Spacer(Modifier.height(spacing.sm))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
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

                is MarkdownBlock.Blank -> {
                    Spacer(Modifier.height(spacing.xs))
                }
            }
        }
    }
}

@Composable
private fun MarkdownHeading(
    text: String,
    level: Int,
    isFirstBlock: Boolean,
) {
    val colors = CrewWikiDesignTokens.colors

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

// ── 표 렌더러 ──────────────────────────────────────────────────────────────────

private val TableBorderColor = Color(0xFF1A1A1A)
private val TableLabelBackground = Color(0xFF1A1A1A)
private val TableLabelTextColor = Color.White
private val TableValueBackground = Color.White
private val TableValueTextColor = Color(0xFF1A1A1A)
private val TableOuterShape = RoundedCornerShape(16.dp)

@Composable
private fun MarkdownTable(table: MarkdownBlock.Table) {
    val colCount = table.headers.size
    // 마크다운 문법상 첫 행은 헤더로 파싱되지만, 라벨/값 카드 스타일에서는
    // 모든 행을 동일하게 "1열 = 라벨, 나머지 열 = 값"으로 취급한다.
    val allRows = listOf(table.headers) + table.rows
    val rowCount = allRows.size

    // 열 수가 많을 수 있으므로 가로 스크롤 지원
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clip(TableOuterShape)
            .border(1.5.dp, TableBorderColor, TableOuterShape),
    ) {
        TableGrid(
            colCount = colCount,
            rowCount = rowCount,
        ) {
            allRows.forEach { row ->
                (0 until colCount).forEach { colIdx ->
                    val cell = row.getOrElse(colIdx) { "" }
                    TableCell(
                        text = parseInline(cell.trim()),
                        isLabelColumn = colIdx == 0,
                    )
                }
            }
        }
    }
}

/**
 * 표를 (colCount × rowCount) 그리드로 배치한다.
 * 1차 측정으로 각 열의 최대 너비/각 행의 최대 높이를 구하고,
 * 2차 측정에서 모든 셀을 해당 너비·높이로 고정해 행/열 경계선이 정확히 맞도록 한다.
 */
@Composable
private fun TableGrid(
    colCount: Int,
    rowCount: Int,
    content: @Composable () -> Unit,
) {
    Layout(content = content) { measurables, _ ->
        require(measurables.size == colCount * rowCount)

        val loose = Constraints()
        val natural = measurables.map { it.measure(loose) }

        val colWidths = IntArray(colCount) { c ->
            (0 until rowCount).maxOf { r -> natural[r * colCount + c].width }
        }
        val rowHeights = IntArray(rowCount) { r ->
            (0 until colCount).maxOf { c -> natural[r * colCount + c].height }
        }

        val placeables = measurables.mapIndexed { idx, measurable ->
            val r = idx / colCount
            val c = idx % colCount
            measurable.measure(Constraints.fixed(max(colWidths[c], 1), max(rowHeights[r], 1)))
        }

        val totalWidth = colWidths.sum()
        val totalHeight = rowHeights.sum()

        layout(totalWidth, totalHeight) {
            var y = 0
            for (r in 0 until rowCount) {
                var x = 0
                for (c in 0 until colCount) {
                    placeables[r * colCount + c].placeRelative(x, y)
                    x += colWidths[c]
                }
                y += rowHeights[r]
            }
        }
    }
}

@Composable
private fun TableCell(
    text: AnnotatedString,
    isLabelColumn: Boolean,
) {
    Box(
        modifier = Modifier
            .background(if (isLabelColumn) TableLabelBackground else TableValueBackground)
            .border(width = 1.dp, color = TableBorderColor),
        contentAlignment = if (isLabelColumn) Alignment.Center else Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isLabelColumn) TableLabelTextColor else TableValueTextColor,
            textAlign = if (isLabelColumn) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}

// ── 블록 타입 ──────────────────────────────────────────────────────────────────

private enum class TableAlign { Start, Center, End }

private sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val annotated: AnnotatedString) : MarkdownBlock
    data class Table(
        val headers: List<String>,
        val alignments: List<TableAlign>,
        val rows: List<List<String>>,
    ) : MarkdownBlock
    data class Image(val alt: String, val url: String, val linkUrl: String? = null) : MarkdownBlock
    data class Code(val language: String, val code: String) : MarkdownBlock
    data object HorizontalRule : MarkdownBlock
    data class ListItem(val ordered: Boolean, val order: Int, val annotated: AnnotatedString) : MarkdownBlock
    data object Blank : MarkdownBlock
}

// ── 파서 ───────────────────────────────────────────────────────────────────────

private val headingRegex = Regex("^(#{1,6})\\s+(.*)")
private val hrRegex = Regex("^[-*_]{3,}\\s*$")
private val orderedListRegex = Regex("^(\\d+)\\.\\s+(.*)")
private val unorderedListRegex = Regex("^[-*+]\\s+(.*)")
private val fenceStart = Regex("^```(\\w*)")
private val tableRowRegex = Regex("^\\|(.+)\\|\\s*$")
private val tableSepRegex = Regex("^\\|[-:| ]+\\|\\s*$")

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val text = raw
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]+>"), "")

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
            blocks += MarkdownBlock.ListItem(
                ordered = true,
                order = olMatch.groupValues[1].toIntOrNull() ?: 1,
                annotated = parseInline(olMatch.groupValues[2]),
            )
            i++; continue
        }

        // 순서 없는 목록
        val ulMatch = unorderedListRegex.find(line)
        if (ulMatch != null) {
            blocks += MarkdownBlock.ListItem(
                ordered = false, order = 0,
                annotated = parseInline(ulMatch.groupValues[1]),
            )
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
                tableRowRegex.matches(next)
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

/** HTML 전처리: <br> → 줄바꿈, 나머지 HTML 태그 제거 */
internal fun String.preprocessMarkdown(): String = this
    .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
    .replace(Regex("<[^>]+>"), "")
    .trimEnd()

private fun String.toVisibleImageCaption(): String? {
    val normalized = trim()
    if (normalized.isBlank()) return null
    if (normalized.equals("image", ignoreCase = true)) return null
    return normalized
}

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
