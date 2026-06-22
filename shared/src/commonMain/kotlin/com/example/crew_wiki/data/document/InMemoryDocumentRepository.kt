package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocument
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.model.RelatedCrewDocument

class InMemoryDocumentRepository : DocumentRepository {
    private val documentsByUuid = listOf(
        CrewWikiDocumentDetail(
            document = CrewWikiDocument(
                documentId = 1,
                documentUUID = "sample-document",
                title = "비모",
                contents = """
                    ## 기본 정보
                    안녕하세요. 저는 크루위키 Android Multiplatform 전환 작업의 샘플 문서입니다.
                    실제 API가 연결되면 이 영역에 서버에서 내려온 마크다운 본문이 들어오게 됩니다.
                    
                    ## 활동과 특징
                    우아한테크코스에서 백엔드 과정을 진행하고 있고, 문서 구조화와 도메인 모델 정리를 좋아합니다.
                    지금 화면은 웹의 문서 상세 레이아웃을 Compose Multiplatform으로 옮긴 첫 번째 버전입니다.
                    
                    ### 관심사
                    안드로이드 멀티플랫폼, 문서 구조화, 개발 생산성 개선에 관심이 있습니다.
                    
                    ## 다음 단계
                    다음으로는 실제 API 연결, 마크다운 렌더링, TOC 이동 처리를 붙일 예정입니다.
                """.trimIndent(),
                writer = "비모",
                generateTime = "2026-06-22T10:54:00",
                organizations = listOf(
                    OrganizationReference(title = "우테코", uuid = "organization-1"),
                    OrganizationReference(title = "백엔드", uuid = "organization-2"),
                ),
            ),
            relatedCrewDocuments = listOf(
                RelatedCrewDocument(documentUuid = "crew-woody", title = "우디"),
                RelatedCrewDocument(documentUuid = "crew-sain", title = "세인"),
                RelatedCrewDocument(documentUuid = "crew-judy", title = "주디"),
            ),
        ),
    ).associateBy { it.document.documentUUID }

    override fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail? {
        return documentsByUuid[documentId]
    }
}
