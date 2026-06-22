package com.example.crew_wiki.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.crew_wiki.CrewWikiDesignTokens

/**
 * KMP iOS 안전 마크다운 렌더러.
 * 외부 라이브러리(mikepenz) 대신 직접 구현하여 iOS SIGABRT 방지.
 * 지원: h1~h3, 단락, **bold**, *italic*, `code`, 이미지, 수평선, 순서/비순서 목록, <br>
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier,
) {
    val blocks = parseMarkdownBlocks(content)
    val colors = CrewWikiDesignTokens.colors
    val spacing = CrewWikiDesignTokens.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    if (index > 0) Spacer(Modifier.height(spacing.lg))
                    Text(
                        text = block.text,
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                            2 -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            else -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        },
                        color = colors.grayscale.c800,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(spacing.sm))
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

                is MarkdownBlock.Image -> {
                    if (index > 0) Spacer(Modifier.height(spacing.md))
                    AsyncImage(
                        model = block.url,
                        contentDescription = block.alt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    )
                    if (block.alt.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = block.alt,
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
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.small,
                            )
                            .padding(12.dp),
                    ) {
                        Text(
                            text = block.code,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(spacing.md))
                }

                is MarkdownBlock.HorizontalRule -> {
                    Spacer(Modifier.height(spacing.md))
                    HorizontalDivider(color = colors.grayscale.c200)
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

// ── 블록 타입 ──────────────────────────────────────────────────────────────────

private sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val annotated: AnnotatedString) : MarkdownBlock
    data class Image(val alt: String, val url: String) : MarkdownBlock
    data class Code(val language: String, val code: String) : MarkdownBlock
    data object HorizontalRule : MarkdownBlock
    data class ListItem(val ordered: Boolean, val order: Int, val annotated: AnnotatedString) : MarkdownBlock
    data object Blank : MarkdownBlock
}

// ── 파서 ───────────────────────────────────────────────────────────────────────

private val headingRegex = Regex("^(#{1,6})\\s+(.*)")
private val imageRegex = Regex("!\\[([^]]*)]\\(([^)]+)\\)")
private val hrRegex = Regex("^[-*_]{3,}\\s*$")
private val orderedListRegex = Regex("^(\\d+)\\.\\s+(.*)")
private val unorderedListRegex = Regex("^[-*+]\\s+(.*)")
private val fenceStart = Regex("^```(\\w*)")

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    // HTML br → 빈 줄로 변환
    val text = raw
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(Regex("<[^>]+>"), "")  // 나머지 HTML 태그 제거

    val blocks = mutableListOf<MarkdownBlock>()
    val lines = text.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // 코드 블록
        val fenceMatch = fenceStart.find(line)
        if (fenceMatch != null) {
            val lang = fenceMatch.groupValues[1]
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].startsWith("```")) {
                codeLines += lines[i]
                i++
            }
            blocks += MarkdownBlock.Code(lang, codeLines.joinToString("\n"))
            i++
            continue
        }

        // 이미지
        val imgMatch = imageRegex.find(line)
        if (imgMatch != null && line.trim().startsWith("!")) {
            blocks += MarkdownBlock.Image(imgMatch.groupValues[1], imgMatch.groupValues[2])
            i++
            continue
        }

        // 수평선
        if (hrRegex.matches(line)) {
            blocks += MarkdownBlock.HorizontalRule
            i++
            continue
        }

        // 제목
        val headingMatch = headingRegex.find(line)
        if (headingMatch != null) {
            blocks += MarkdownBlock.Heading(
                level = headingMatch.groupValues[1].length,
                text = headingMatch.groupValues[2].trim(),
            )
            i++
            continue
        }

        // 순서 있는 목록
        val olMatch = orderedListRegex.find(line)
        if (olMatch != null) {
            blocks += MarkdownBlock.ListItem(
                ordered = true,
                order = olMatch.groupValues[1].toIntOrNull() ?: 1,
                annotated = parseInline(olMatch.groupValues[2]),
            )
            i++
            continue
        }

        // 순서 없는 목록
        val ulMatch = unorderedListRegex.find(line)
        if (ulMatch != null) {
            blocks += MarkdownBlock.ListItem(
                ordered = false,
                order = 0,
                annotated = parseInline(ulMatch.groupValues[1]),
            )
            i++
            continue
        }

        // 빈 줄
        if (line.isBlank()) {
            if (blocks.lastOrNull() !is MarkdownBlock.Blank) {
                blocks += MarkdownBlock.Blank
            }
            i++
            continue
        }

        // 일반 단락 (연속 줄 합치기)
        val paragraphLines = mutableListOf(line)
        while (i + 1 < lines.size) {
            val next = lines[i + 1]
            if (next.isBlank() || headingRegex.containsMatchIn(next) ||
                hrRegex.matches(next) || fenceStart.containsMatchIn(next)
            ) break
            paragraphLines += next
            i++
        }
        blocks += MarkdownBlock.Paragraph(parseInline(paragraphLines.joinToString(" ")))
        i++
    }

    return blocks
}

// ── 인라인 파서 (**bold**, *italic*, `code`, [link](url)) ────────────────────

private fun parseInline(text: String): AnnotatedString = buildAnnotatedString {
    var pos = 0
    while (pos < text.length) {
        when {
            // **bold** 또는 __bold__
            pos + 1 < text.length && (text.startsWith("**", pos) || text.startsWith("__", pos)) -> {
                val marker = text.substring(pos, pos + 2)
                val end = text.indexOf(marker, pos + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(text.substring(pos + 2, end))
                    pop()
                    pos = end + 2
                } else { append(text[pos]); pos++ }
            }
            // *italic* 또는 _italic_
            text[pos] == '*' || text[pos] == '_' -> {
                val marker = text[pos].toString()
                val end = text.indexOf(marker, pos + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(text.substring(pos + 1, end))
                    pop()
                    pos = end + 1
                } else { append(text[pos]); pos++ }
            }
            // `inline code`
            text[pos] == '`' -> {
                val end = text.indexOf('`', pos + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x1A000000)))
                    append(text.substring(pos + 1, end))
                    pop()
                    pos = end + 1
                } else { append(text[pos]); pos++ }
            }
            // [link text](url) — 링크 텍스트만 표시
            text[pos] == '[' -> {
                val closeBracket = text.indexOf(']', pos + 1)
                val openParen = if (closeBracket != -1) text.indexOf('(', closeBracket) else -1
                val closeParen = if (openParen == closeBracket + 1) text.indexOf(')', openParen + 1) else -1
                if (closeParen != -1) {
                    val linkText = text.substring(pos + 1, closeBracket)
                    pushStyle(SpanStyle(color = Color(0xFF1A73E8), textDecoration = TextDecoration.Underline))
                    append(linkText)
                    pop()
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
