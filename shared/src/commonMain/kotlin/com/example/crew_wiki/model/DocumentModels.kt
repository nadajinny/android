package com.example.crew_wiki.model

import kotlinx.serialization.Serializable

@Serializable
enum class DocumentType {
    CREW,
    ORGANIZATION,
}

@Serializable
data class OrganizationReference(
    val title: String,
    val uuid: String,
)

@Serializable
data class CrewWikiDocument(
    val documentId: Long,
    val documentUUID: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val organizations: List<OrganizationReference> = emptyList(),
)

@Serializable
data class LatestCrewWikiDocument(
    val document: CrewWikiDocument,
    val latestVersion: Int,
)

@Serializable
data class WriteDocumentContent(
    val title: String,
    val contents: String,
    val writer: String,
    val documentBytes: Long,
)

@Serializable
data class DocumentLogSummary(
    val id: Long,
    val title: String,
    val version: Int,
    val writer: String,
    val documentBytes: Long,
    val generateTime: String,
)

@Serializable
data class DocumentLogDetail(
    val contents: String,
    val generateTime: String,
    val logId: Long,
    val title: String,
    val writer: String,
)

@Serializable
data class PopularDocument(
    val id: Long,
    val title: String,
    val viewCount: Int,
    val editCount: Int,
)

@Serializable
enum class PopularSortType {
    VIEWS,
    EDITS,
}

@Serializable
data class ExpandedDocument(
    val id: Long,
    val uuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val documentBytes: Long,
    val viewCount: Int,
    val documentType: DocumentType,
    val organizations: List<OrganizationReference> = emptyList(),
)

@Serializable
data class PostDocumentBody(
    val title: String,
    val contents: String,
    val writer: String,
    val documentBytes: Long,
    val uuid: String,
)

@Serializable
data class PostDocumentContent(
    val body: PostDocumentBody,
    val newOrganizations: List<OrganizationReference> = emptyList(),
    val existingOrganizations: List<OrganizationReference> = emptyList(),
)
