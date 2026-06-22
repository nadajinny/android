package com.example.crew_wiki.network.dto

import kotlinx.serialization.Serializable

// ── 공통 래퍼 ─────────────────────────────────────────────────────────────────

@Serializable
data class ApiResponse<T>(
    val data: T,
    val code: String,
)

@Serializable
data class PagedResponseDto<T>(
    val page: Int,
    val totalPage: Int,
    val data: List<T>,
)

// ── 문서 (DocumentResponse) ───────────────────────────────────────────────────
// GET /document/uuid/{uuidText}
// GET /document/title/{title}
// GET /document/random

@Serializable
data class OrganizationDocumentResponseDto(
    val organizationDocumentId: Long,
    val organizationDocumentUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
)

@Serializable
data class DocumentResponseDto(
    val documentId: Long,
    val documentUUID: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val viewCount: Int = 0,
    val latestVersion: Long = 0,
    val organizationDocumentResponses: List<OrganizationDocumentResponseDto> = emptyList(),
)

// ── 문서 목록 (DocumentListResponse) ─────────────────────────────────────────
// GET /document  (페이지네이션)

@Serializable
data class DocumentListResponseDto(
    val id: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val documentBytes: Long,
    val generateTime: String,
    val uuid: String,
    val viewCount: Int = 0,
    val documentType: String,   // "CREW" | "ORGANIZATION"
)

// ── 검색 (DocumentSearchResponse) ────────────────────────────────────────────
// GET /document/search?keyWord=

@Serializable
data class DocumentSearchResponseDto(
    val title: String,
    val uuid: String,
    val documentType: String,
)

// ── 조직 문서 검색 응답 ────────────────────────────────────────────────────────
// GET /document/{uuidText}/organization-documents

@Serializable
data class OrganizationDocumentSearchResponseDto(
    val uuid: String,
    val title: String,
)

// ── 히스토리 목록 (HistoryResponse) ──────────────────────────────────────────
// GET /document/uuid/{uuidText}/log

@Serializable
data class HistoryResponseDto(
    val id: Long,
    val title: String,
    val version: Long,
    val writer: String,
    val documentBytes: Long,
    val generateTime: String,
)

// ── 히스토리 상세 (HistoryDetailResponse) ─────────────────────────────────────
// GET /document/log/{logId}

@Serializable
data class HistoryDetailResponseDto(
    val logId: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
)

// ── 조직(그룹) 문서 + 이벤트 (OrganizationDocumentAndEventResponse) ─────────
// GET /organization/uuid/{uuidText}

@Serializable
data class OrganizationEventResponseDto(
    val organizationEventUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val occurredAt: String,   // format: date (yyyy-MM-dd)
)

@Serializable
data class LinkedCrewDocumentResponseDto(
    val documentUuid: String,
    val title: String,
)

@Serializable
data class OrganizationDocumentAndEventResponseDto(
    val organizationDocumentId: Long,
    val organizationDocumentUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val organizationEventResponses: List<OrganizationEventResponseDto> = emptyList(),
    val linkedCrewDocuments: List<LinkedCrewDocumentResponseDto> = emptyList(),
)
