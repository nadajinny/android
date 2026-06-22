package com.example.crew_wiki.model

import kotlinx.serialization.Serializable

@Serializable
enum class DocumentType {
    CREW,
    ORGANIZATION,
}

/**
 * 문서에 연결된 조직(그룹) 문서 참조
 * Swagger: OrganizationDocumentResponse
 */
@Serializable
data class OrganizationReference(
    val organizationDocumentId: Long,
    val organizationDocumentUuid: String,
    val title: String,
)

/**
 * 크루 문서
 * Swagger: DocumentResponse
 */
@Serializable
data class CrewWikiDocument(
    val documentId: Long,
    val documentUUID: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val viewCount: Int = 0,
    val latestVersion: Long = 0,
    val organizations: List<OrganizationReference> = emptyList(),
)

@Serializable
data class CrewWikiDocumentDetail(
    val document: CrewWikiDocument,
    // 조직 문서에서 linkedCrewDocuments로 조회되는 연관 크루 문서
    val relatedCrewDocuments: List<RelatedCrewDocument> = emptyList(),
)

@Serializable
data class RelatedCrewDocument(
    val documentUuid: String,
    val title: String,
)

/**
 * 편집 기록 목록 항목
 * Swagger: HistoryResponse
 */
@Serializable
data class DocumentLogSummary(
    val id: Long,
    val title: String,
    val version: Long,        // Swagger: int64
    val writer: String,
    val documentBytes: Long,
    val generateTime: String,
)

/**
 * 편집 기록 상세
 * Swagger: HistoryDetailResponse
 */
@Serializable
data class DocumentLogDetail(
    val logId: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
)

/**
 * 인기 문서 (GET /document 정렬 결과)
 * Swagger: DocumentListResponse — editCount 없음
 */
@Serializable
data class PopularDocument(
    val id: Long,
    val documentUUID: String,
    val title: String,
    val viewCount: Int,
)

@Serializable
enum class PopularSortType {
    VIEWS,
    // EDITS: 서버 API에 editCount 미제공 → 조회수 정렬로 대체
}
