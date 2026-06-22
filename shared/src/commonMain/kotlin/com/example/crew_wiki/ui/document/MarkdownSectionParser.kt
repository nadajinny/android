package com.example.crew_wiki.ui.document

import com.example.crew_wiki.ui.group.GroupSectionUiModel

/** 마크다운 문자열을 섹션 목록으로 파싱하는 공통 유틸 */
fun parseGroupSections(contents: String): List<GroupSectionUiModel> {
    val sections = mutableListOf<GroupSectionUiModel>()
    var currentLevel = 1
    var currentHeading = ""
    val currentParagraphs = mutableListOf<String>()

    fun flush() {
        val paragraphs = currentParagraphs.map(String::trim).filter(String::isNotEmpty)
        if (currentHeading.isNotBlank() || paragraphs.isNotEmpty()) {
            sections += GroupSectionUiModel(
                level = currentLevel,
                heading = currentHeading,
                paragraphs = paragraphs.ifEmpty { listOf("내용이 없습니다.") },
            )
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
        listOf(GroupSectionUiModel(1, "본문", listOf(contents)))
    }
}
