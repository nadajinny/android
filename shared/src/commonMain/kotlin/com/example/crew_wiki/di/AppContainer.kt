package com.example.crew_wiki.di

import com.example.crew_wiki.data.document.NetworkDocumentRepository
import com.example.crew_wiki.data.group.GroupDocumentRepository
import com.example.crew_wiki.network.DocumentApiService
import com.example.crew_wiki.network.GroupApiService
import com.example.crew_wiki.network.createCrewWikiHttpClient

/**
 * 앱 전역 의존성 컨테이너 (간단한 수동 DI)
 * 실제 프로젝트에서는 Koin/Dagger 등으로 대체 가능
 */
object AppContainer {
    private val httpClient by lazy { createCrewWikiHttpClient() }

    val documentApiService by lazy { DocumentApiService(httpClient) }
    val groupApiService by lazy { GroupApiService(httpClient) }

    val documentRepository by lazy { NetworkDocumentRepository(documentApiService) }
    val groupDocumentRepository by lazy { GroupDocumentRepository(groupApiService) }
}
