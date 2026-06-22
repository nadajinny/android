package com.example.crew_wiki.data.group

import com.example.crew_wiki.model.GroupDocumentDetail
import com.example.crew_wiki.model.LinkedCrewDocument
import com.example.crew_wiki.model.OrganizationEvent
import com.example.crew_wiki.network.GroupApiService

class GroupDocumentRepository(
    private val apiService: GroupApiService,
) {
    suspend fun fetchGroupDocumentByUUID(uuid: String): GroupDocumentDetail {
        val dto = apiService.getGroupDocumentByUUID(uuid)
        return GroupDocumentDetail(
            organizationDocumentId = dto.organizationDocumentId,
            organizationDocumentUuid = dto.organizationDocumentUuid,
            title = dto.title,
            contents = dto.contents,
            writer = dto.writer,
            generateTime = dto.generateTime,
            events = dto.organizationEventResponses.map { event ->
                OrganizationEvent(
                    organizationEventUuid = event.organizationEventUuid,
                    title = event.title,
                    contents = event.contents,
                    writer = event.writer,
                    occurredAt = event.occurredAt,
                )
            },
            linkedCrewDocuments = dto.linkedCrewDocuments.map { crew ->
                LinkedCrewDocument(
                    documentUuid = crew.documentUuid,
                    title = crew.title,
                )
            },
        )
    }
}
