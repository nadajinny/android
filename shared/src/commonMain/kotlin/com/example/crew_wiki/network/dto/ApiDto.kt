package com.example.crew_wiki.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T,
    val code: String,
)

@Serializable
data class PaginationResponseDto<T>(
    val page: Int,
    val totalPage: Int,
    val data: List<T>,
)

@Serializable
data class OrganizationReferenceDto(
    val title: String,
    val uuid: String,
)

// Document DTOs
@Serializable
data class WikiDocumentDto(
    val documentId: Long,
    val documentUUID: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val organizations: List<OrganizationReferenceDto> = emptyList(),
)

@Serializable
data class LatestWikiDocumentDto(
    val documentId: Long,
    val documentUUID: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val organizations: List<OrganizationReferenceDto> = emptyList(),
    val latestVersion: Int = 0,
)

@Serializable
data class DocumentLogSummaryDto(
    val id: Long,
    val title: String,
    val version: Int,
    val writer: String,
    val documentBytes: Long,
    val generateTime: String,
)

@Serializable
data class DocumentLogDetailDto(
    val contents: String,
    val generateTime: String,
    val logId: Long,
    val title: String,
    val writer: String,
)

@Serializable
data class ExpandedDocumentDto(
    val id: Long,
    val uuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val documentBytes: Long,
    val viewCount: Int,
    val editCount: Int = 0,
    val documentType: String,
    val organizations: List<OrganizationReferenceDto> = emptyList(),
)

// Organization (Group) DTOs
@Serializable
data class OrganizationEventResponseDto(
    val organizationEventUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val occurredAt: String,
)

@Serializable
data class LinkedCrewDocumentDto(
    val documentUuid: String,
    val title: String,
)

@Serializable
data class OrganizationDocumentWithEventsDto(
    val organizationDocumentId: Long,
    val organizationDocumentUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val organizationEventResponses: List<OrganizationEventResponseDto> = emptyList(),
    val linkedCrewDocuments: List<LinkedCrewDocumentDto> = emptyList(),
)
