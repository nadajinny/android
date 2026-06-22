package com.example.crew_wiki.data.group

import com.example.crew_wiki.model.GroupDocumentDetail
import com.example.crew_wiki.model.LinkedCrewDocument
import com.example.crew_wiki.model.OrganizationEvent
import com.example.crew_wiki.network.GroupApiService

class GroupDocumentRepository(
    private val apiService: GroupApiService,
) {
    // GET /organization/uuid/{uuidText}
    suspend fun fetchGroupDocumentByUUID(uuid: String): GroupDocumentDetail {
        val dto = apiService.getGroupDocumentByUUID(uuid)
        return GroupDocumentDetail(
            organizationDocumentId = dto.organizationDocumentId,
            organizationDocumentUuid = dto.organizationDocumentUuid,
            title = dto.title,
            contents = dto.contents,
            writer = dto.writer,
            generateTime = dto.generateTime,
            events = dto.organizationEventResponses.map { e ->
                OrganizationEvent(
                    organizationEventUuid = e.organizationEventUuid,
                    title = e.title,
                    contents = e.contents,
                    writer = e.writer,
                    occurredAt = e.occurredAt,   // yyyy-MM-dd
                )
            },
            linkedCrewDocuments = dto.linkedCrewDocuments.map { c ->
                LinkedCrewDocument(
                    documentUuid = c.documentUuid,
                    title = c.title,
                )
            },
        )
    }
}
