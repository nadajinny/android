package com.example.crew_wiki.network

import com.example.crew_wiki.network.dto.ApiResponse
import com.example.crew_wiki.network.dto.OrganizationDocumentAndEventResponseDto
import com.example.crew_wiki.network.dto.OrganizationDocumentCreateRequestDto
import com.example.crew_wiki.network.dto.OrganizationDocumentLinkRequestDto
import com.example.crew_wiki.network.dto.OrganizationDocumentResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class GroupApiService(private val client: HttpClient) {

    // GET /organization/uuid/{uuidText}
    suspend fun getGroupDocumentByUUID(uuid: String): OrganizationDocumentAndEventResponseDto =
        client.get("$BASE_URL/organization/uuid/$uuid")
            .body<ApiResponse<OrganizationDocumentAndEventResponseDto>>().data

    suspend fun postOrganizationDocument(
        request: OrganizationDocumentCreateRequestDto,
    ): OrganizationDocumentResponseDto =
        client.post("$BASE_URL/organization") {
            setBody(request)
        }.body<ApiResponse<OrganizationDocumentResponseDto>>().data

    suspend fun linkOrganizationDocument(
        request: OrganizationDocumentLinkRequestDto,
    ): OrganizationDocumentResponseDto =
        client.post("$BASE_URL/organization/link") {
            setBody(request)
        }.body<ApiResponse<OrganizationDocumentResponseDto>>().data
}
