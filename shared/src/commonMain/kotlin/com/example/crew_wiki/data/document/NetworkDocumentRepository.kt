package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocument
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.DocumentLogDetail
import com.example.crew_wiki.model.DocumentLogSummary
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.model.PopularSortType
import com.example.crew_wiki.model.RelatedCrewDocument
import com.example.crew_wiki.network.DocumentApiService
import com.example.crew_wiki.network.dto.WikiDocumentDto

class NetworkDocumentRepository(
    private val apiService: DocumentApiService,
) : DocumentRepository {

    override fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail? {
        // 동기 인터페이스는 유지하되 실제로는 suspend 버전 사용
        return null
    }

    override fun getPopularDocuments(sortType: PopularSortType): List<PopularDocument> {
        return emptyList()
    }

    // ── suspend 버전 (ViewModel에서 사용) ────────────────────────

    suspend fun fetchDocumentByUUID(uuid: String): CrewWikiDocumentDetail {
        val dto = apiService.getDocumentByUUID(uuid)
        return dto.toDomainDetail()
    }

    suspend fun fetchDocumentLogsByUUID(
        uuid: String,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): Pair<List<DocumentLogSummary>, Int> {
        val page = apiService.getDocumentLogsByUUID(uuid, pageNumber, pageSize)
        val summaries = page.data.map { dto ->
            DocumentLogSummary(
                id = dto.id,
                title = dto.title,
                version = dto.version,
                writer = dto.writer,
                documentBytes = dto.documentBytes,
                generateTime = dto.generateTime,
            )
        }
        return summaries to page.totalPage
    }

    suspend fun fetchDocumentLog(logId: Long): DocumentLogDetail {
        val dto = apiService.getDocumentLog(logId)
        return DocumentLogDetail(
            contents = dto.contents,
            generateTime = dto.generateTime,
            logId = dto.logId,
            title = dto.title,
            writer = dto.writer,
        )
    }

    suspend fun fetchPopularDocuments(sortType: PopularSortType): List<PopularDocument> {
        val sort = when (sortType) {
            PopularSortType.VIEWS -> "viewCount"
            PopularSortType.EDITS -> "editCount"
        }
        val page = apiService.getDocuments(pageSize = 10, sort = sort, sortDirection = "DESC")
        return page.data.mapIndexed { _, dto ->
            PopularDocument(
                id = dto.id,
                documentUUID = dto.uuid,
                title = dto.title,
                viewCount = dto.viewCount,
                editCount = dto.editCount,
            )
        }
    }

    // ── 매핑 헬퍼 ─────────────────────────────────────────────

    private fun WikiDocumentDto.toDomainDetail(): CrewWikiDocumentDetail {
        val document = CrewWikiDocument(
            documentId = documentId,
            documentUUID = documentUUID,
            title = title,
            contents = contents,
            writer = writer,
            generateTime = generateTime,
            organizations = organizations.map { OrganizationReference(it.title, it.uuid) },
        )
        return CrewWikiDocumentDetail(
            document = document,
            relatedCrewDocuments = emptyList(), // 별도 API 미제공
        )
    }
}
