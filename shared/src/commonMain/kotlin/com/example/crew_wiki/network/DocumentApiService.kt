package com.example.crew_wiki.network

import com.example.crew_wiki.network.dto.ApiResponse
import com.example.crew_wiki.network.dto.DocumentLogDetailDto
import com.example.crew_wiki.network.dto.DocumentLogSummaryDto
import com.example.crew_wiki.network.dto.ExpandedDocumentDto
import com.example.crew_wiki.network.dto.PaginationResponseDto
import com.example.crew_wiki.network.dto.WikiDocumentDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class DocumentApiService(private val client: HttpClient) {

    // GET /document/uuid/{uuid}
    suspend fun getDocumentByUUID(uuid: String): WikiDocumentDto {
        val response = client.get("$BASE_URL/document/uuid/$uuid")
        return response.body<ApiResponse<WikiDocumentDto>>().data
    }

    // GET /document/uuid/{uuid}/log
    suspend fun getDocumentLogsByUUID(
        uuid: String,
        pageNumber: Int = 0,
        pageSize: Int = 10,
    ): PaginationResponseDto<DocumentLogSummaryDto> {
        val response = client.get("$BASE_URL/document/uuid/$uuid/log") {
            parameter("pageNumber", pageNumber)
            parameter("pageSize", pageSize)
            parameter("sort", "id")
            parameter("sortDirection", "DESC")
        }
        return response.body<ApiResponse<PaginationResponseDto<DocumentLogSummaryDto>>>().data
    }

    // GET /document/log/{logId}
    suspend fun getDocumentLog(logId: Long): DocumentLogDetailDto {
        val response = client.get("$BASE_URL/document/log/$logId")
        return response.body<ApiResponse<DocumentLogDetailDto>>().data
    }

    // GET /document  (정렬 기준으로 인기 문서 조회)
    suspend fun getDocuments(
        pageNumber: Int = 0,
        pageSize: Int = 10,
        sort: String = "viewCount",
        sortDirection: String = "DESC",
    ): PaginationResponseDto<ExpandedDocumentDto> {
        val response = client.get("$BASE_URL/document") {
            parameter("pageNumber", pageNumber)
            parameter("pageSize", pageSize)
            parameter("sort", sort)
            parameter("sortDirection", sortDirection)
        }
        return response.body<ApiResponse<PaginationResponseDto<ExpandedDocumentDto>>>().data
    }

    // GET /document/search?keyWord=
    suspend fun searchDocuments(keyword: String): List<WikiDocumentDto> {
        val response = client.get("$BASE_URL/document/search") {
            parameter("keyWord", keyword)
        }
        return response.body<ApiResponse<List<WikiDocumentDto>>>().data
    }
}
