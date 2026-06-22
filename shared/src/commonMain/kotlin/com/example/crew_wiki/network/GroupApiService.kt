package com.example.crew_wiki.network

import com.example.crew_wiki.network.dto.ApiResponse
import com.example.crew_wiki.network.dto.OrganizationDocumentWithEventsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GroupApiService(private val client: HttpClient) {

    // GET /organization/uuid/{uuid}
    suspend fun getGroupDocumentByUUID(uuid: String): OrganizationDocumentWithEventsDto {
        val response = client.get("$BASE_URL/organization/uuid/$uuid")
        return response.body<ApiResponse<OrganizationDocumentWithEventsDto>>().data
    }
}
