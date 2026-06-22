package com.example.crew_wiki.network

import com.example.crew_wiki.network.dto.ApiResponse
import com.example.crew_wiki.network.dto.DocumentListResponseDto
import com.example.crew_wiki.network.dto.DocumentResponseDto
import com.example.crew_wiki.network.dto.DocumentSaveRequestDto
import com.example.crew_wiki.network.dto.DocumentSearchResponseDto
import com.example.crew_wiki.network.dto.HistoryDetailResponseDto
import com.example.crew_wiki.network.dto.HistoryResponseDto
import com.example.crew_wiki.network.dto.OrganizationDocumentSearchResponseDto
import com.example.crew_wiki.network.dto.PagedResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class DocumentApiService(private val client: HttpClient) {

    // GET /document/uuid/{uuidText}
    suspend fun getDocumentByUUID(uuid: String): DocumentResponseDto =
        client.get("$BASE_URL/document/uuid/$uuid")
            .body<ApiResponse<DocumentResponseDto>>().data

    // GET /document/title/{title}
    suspend fun getDocumentByTitle(title: String): DocumentResponseDto =
        client.get("$BASE_URL/document/title/$title")
            .body<ApiResponse<DocumentResponseDto>>().data

    // GET /document/random
    suspend fun getRandomDocument(): DocumentResponseDto =
        client.get("$BASE_URL/document/random")
            .body<ApiResponse<DocumentResponseDto>>().data

    // GET /document  (페이지네이션 + 정렬)
    suspend fun getDocuments(
        pageNumber: Int = 0,
        pageSize: Int = 10,
        sort: String = "viewCount",
        sortDirection: String = "DESC",
    ): PagedResponseDto<DocumentListResponseDto> =
        client.get("$BASE_URL/document") {
            parameter("pageNumber", pageNumber)
            parameter("pageSize", pageSize)
            parameter("sort", sort)
            parameter("sortDirection", sortDirection)
        }.body<ApiResponse<PagedResponseDto<DocumentListResponseDto>>>().data

    // GET /document/uuid/{uuidText}/log
    suspend fun getDocumentLogs(
        uuid: String,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): PagedResponseDto<HistoryResponseDto> =
        client.get("$BASE_URL/document/uuid/$uuid/log") {
            parameter("pageNumber", pageNumber)
            parameter("pageSize", pageSize)
            parameter("sort", "id")
            parameter("sortDirection", "DESC")
        }.body<ApiResponse<PagedResponseDto<HistoryResponseDto>>>().data

    // GET /document/log/{logId}
    suspend fun getDocumentLog(logId: Long): HistoryDetailResponseDto =
        client.get("$BASE_URL/document/log/$logId")
            .body<ApiResponse<HistoryDetailResponseDto>>().data

    // GET /document/search?keyWord=
    suspend fun searchDocuments(keyword: String): List<DocumentSearchResponseDto> =
        client.get("$BASE_URL/document/search") {
            parameter("keyWord", keyword)
        }.body<ApiResponse<List<DocumentSearchResponseDto>>>().data

    // GET /document/{uuidText}/organization-documents
    suspend fun getOrganizationDocumentsByDocumentUUID(
        uuid: String,
    ): List<OrganizationDocumentSearchResponseDto> =
        client.get("$BASE_URL/document/$uuid/organization-documents")
            .body<ApiResponse<List<OrganizationDocumentSearchResponseDto>>>().data

    suspend fun postDocument(request: DocumentSaveRequestDto): DocumentResponseDto =
        client.post("$BASE_URL/document") {
            setBody(request)
        }.body<ApiResponse<DocumentResponseDto>>().data

    suspend fun putDocument(request: DocumentSaveRequestDto): DocumentResponseDto =
        client.put("$BASE_URL/document") {
            setBody(request)
        }.body<ApiResponse<DocumentResponseDto>>().data

    suspend fun deleteOrganizationFromDocument(
        documentUuid: String,
        organizationDocumentUuid: String,
    ) {
        client.delete("$BASE_URL/document/$documentUuid/organization-documents/$organizationDocumentUuid")
    }
}
