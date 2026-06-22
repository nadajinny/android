package com.example.crew_wiki.network

import com.example.crew_wiki.network.dto.ApiResponse
import com.example.crew_wiki.network.dto.OrganizationDocumentAndEventResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GroupApiService(private val client: HttpClient) {

    // GET /organization/uuid/{uuidText}
    suspend fun getGroupDocumentByUUID(uuid: String): OrganizationDocumentAndEventResponseDto =
        client.get("$BASE_URL/organization/uuid/$uuid")
            .body<ApiResponse<OrganizationDocumentAndEventResponseDto>>().data
}
