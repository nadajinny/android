package com.example.crew_wiki.model

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationEvent(
    val organizationEventUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val occurredAt: String,
)

@Serializable
data class LinkedCrewDocument(
    val documentUuid: String,
    val title: String,
)

@Serializable
data class GroupDocumentDetail(
    val organizationDocumentId: Long,
    val organizationDocumentUuid: String,
    val title: String,
    val contents: String,
    val writer: String,
    val generateTime: String,
    val events: List<OrganizationEvent> = emptyList(),
    val linkedCrewDocuments: List<LinkedCrewDocument> = emptyList(),
)
