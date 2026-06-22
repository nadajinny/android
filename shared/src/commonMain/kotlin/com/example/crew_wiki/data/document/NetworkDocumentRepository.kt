package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocument
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.DocumentLogDetail
import com.example.crew_wiki.model.DocumentLogSummary
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.model.PopularSortType
import com.example.crew_wiki.network.DocumentApiService
import com.example.crew_wiki.network.dto.DocumentResponseDto

class NetworkDocumentRepository(
    private val apiService: DocumentApiService,
) : DocumentRepository {

    override fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail? = null
    override fun getPopularDocuments(sortType: PopularSortType): List<PopularDocument> = emptyList()

    // ── 문서 상세 ──────────────────────────────────────────────────────────────

    suspend fun fetchDocumentByUUID(uuid: String): CrewWikiDocumentDetail {
        val dto = apiService.getDocumentByUUID(uuid)
        return dto.toDomain()
    }

    // ── 편집 기록 ──────────────────────────────────────────────────────────────

    /** @return Pair(로그 목록, totalPage) */
    suspend fun fetchDocumentLogs(
        uuid: String,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): Pair<List<DocumentLogSummary>, Int> {
        val page = apiService.getDocumentLogs(uuid, pageNumber, pageSize)
        return page.data.map { dto ->
            DocumentLogSummary(
                id = dto.id,
                title = dto.title,
                version = dto.version,
                writer = dto.writer,
                documentBytes = dto.documentBytes,
                generateTime = dto.generateTime,
            )
        } to page.totalPage
    }

    suspend fun fetchDocumentLog(logId: Long): DocumentLogDetail {
        val dto = apiService.getDocumentLog(logId)
        return DocumentLogDetail(
            logId = dto.logId,
            title = dto.title,
            contents = dto.contents,
            writer = dto.writer,
            generateTime = dto.generateTime,
        )
    }

    // ── 인기 문서 (viewCount 기준) ─────────────────────────────────────────────
    // Swagger: DocumentListResponse에 editCount 없음 → viewCount 정렬만 지원

    suspend fun fetchPopularDocuments(sortType: PopularSortType): List<PopularDocument> {
        val page = apiService.getDocuments(
            pageNumber = 0,
            pageSize = 10,
            sort = "viewCount",
            sortDirection = "DESC",
        )
        return page.data.map { dto ->
            PopularDocument(
                id = dto.id,
                documentUUID = dto.uuid,
                title = dto.title,
                viewCount = dto.viewCount,
            )
        }
    }

    // ── 매핑 ──────────────────────────────────────────────────────────────────

    private fun DocumentResponseDto.toDomain(): CrewWikiDocumentDetail {
        val document = CrewWikiDocument(
            documentId = documentId,
            documentUUID = documentUUID,
            title = title,
            contents = contents,
            writer = writer,
            generateTime = generateTime,
            viewCount = viewCount,
            latestVersion = latestVersion,
            organizations = organizationDocumentResponses.map { org ->
                OrganizationReference(
                    organizationDocumentId = org.organizationDocumentId,
                    organizationDocumentUuid = org.organizationDocumentUuid,
                    title = org.title,
                )
            },
        )
        return CrewWikiDocumentDetail(document = document)
    }
}
