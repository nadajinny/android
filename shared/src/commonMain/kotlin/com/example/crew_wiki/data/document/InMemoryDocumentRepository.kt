package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocument
import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.OrganizationReference
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.model.PopularSortType
import com.example.crew_wiki.model.RelatedCrewDocument

class InMemoryDocumentRepository : DocumentRepository {
    private val documentDetails = listOf(
        sampleDocumentDetail(
            documentId = 1,
            uuid = "sample-document",
            title = "비모",
            writer = "비모",
            organizations = listOf("우테코", "백엔드"),
            relatedCrews = listOf("우디", "세인", "주디"),
            contents = """
                ## 기본 정보
                안녕하세요. 저는 크루위키 Android Multiplatform 전환 작업의 샘플 문서입니다.
                실제 API가 연결되면 이 영역에 서버에서 내려온 마크다운 본문이 들어오게 됩니다.

                ## 활동과 특징
                우아한테크코스에서 백엔드 과정을 진행하고 있고, 문서 구조화와 도메인 모델 정리를 좋아합니다.
                지금 화면은 웹의 문서 상세 레이아웃을 Compose Multiplatform으로 옮긴 첫 번째 버전입니다.

                ### 관심사
                안드로이드 멀티플랫폼, 문서 구조화, 개발 생산성 개선에 관심이 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 2,
            uuid = "crew-woody",
            title = "우디",
            writer = "우디",
            organizations = listOf("우테코", "프론트엔드"),
            relatedCrews = listOf("비모", "주디"),
            contents = """
                ## 기본 정보
                프론트엔드 크루 우디의 소개 문서입니다.

                ## 활동과 특징
                사용성 좋은 인터페이스와 디자인 시스템 구축에 관심이 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 3,
            uuid = "crew-sain",
            title = "세인",
            writer = "세인",
            organizations = listOf("우테코", "백엔드"),
            relatedCrews = listOf("비모", "우디"),
            contents = """
                ## 기본 정보
                백엔드 크루 세인의 문서입니다.

                ## 활동과 특징
                서버 성능 최적화와 테스트 자동화에 관심이 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 4,
            uuid = "crew-judy",
            title = "주디",
            writer = "주디",
            organizations = listOf("우테코", "모바일"),
            relatedCrews = listOf("비모", "우디"),
            contents = """
                ## 기본 정보
                모바일 크루 주디의 소개 문서입니다.

                ## 활동과 특징
                Android 품질 개선과 사용자 경험 설계에 관심이 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 5,
            uuid = "crew-river",
            title = "리버",
            writer = "리버",
            organizations = listOf("우테코", "백엔드"),
            relatedCrews = listOf("세인", "비모"),
            contents = """
                ## 기본 정보
                리버의 문서입니다.

                ## 활동과 특징
                데이터 모델링과 클린 아키텍처를 좋아합니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 6,
            uuid = "crew-hazel",
            title = "헤이즐",
            writer = "헤이즐",
            organizations = listOf("우테코", "프론트엔드"),
            relatedCrews = listOf("우디", "주디"),
            contents = """
                ## 기본 정보
                헤이즐의 문서입니다.

                ## 활동과 특징
                접근성과 반응형 UI 구성에 관심이 많습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 7,
            uuid = "crew-noel",
            title = "노엘",
            writer = "노엘",
            organizations = listOf("우테코", "모바일"),
            relatedCrews = listOf("주디", "헤이즐"),
            contents = """
                ## 기본 정보
                노엘의 문서입니다.

                ## 활동과 특징
                Compose와 앱 아키텍처 설계를 주로 다룹니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 8,
            uuid = "crew-summer",
            title = "서머",
            writer = "서머",
            organizations = listOf("우테코", "백엔드"),
            relatedCrews = listOf("리버", "세인"),
            contents = """
                ## 기본 정보
                서머의 문서입니다.

                ## 활동과 특징
                배치 처리와 운영 자동화에 흥미가 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 9,
            uuid = "crew-dawn",
            title = "던",
            writer = "던",
            organizations = listOf("우테코", "프론트엔드"),
            relatedCrews = listOf("우디", "헤이즐"),
            contents = """
                ## 기본 정보
                던의 문서입니다.

                ## 활동과 특징
                제품 경험과 마이크로 인터랙션에 관심이 있습니다.
            """.trimIndent(),
        ),
        sampleDocumentDetail(
            documentId = 10,
            uuid = "crew-mint",
            title = "민트",
            writer = "민트",
            organizations = listOf("우테코", "백엔드"),
            relatedCrews = listOf("비모", "리버"),
            contents = """
                ## 기본 정보
                민트의 문서입니다.

                ## 활동과 특징
                장애 대응과 로깅 체계 설계를 즐깁니다.
            """.trimIndent(),
        ),
    )

    private val documentsByUuid = documentDetails.associateBy { it.document.documentUUID }

    private val popularDocuments = listOf(
        PopularDocument(id = 1, documentUUID = "sample-document", title = "비모", viewCount = 3221),
        PopularDocument(id = 2, documentUUID = "crew-woody", title = "우디", viewCount = 2980),
        PopularDocument(id = 3, documentUUID = "crew-sain", title = "세인", viewCount = 2844),
        PopularDocument(id = 4, documentUUID = "crew-judy", title = "주디", viewCount = 2601),
        PopularDocument(id = 5, documentUUID = "crew-river", title = "리버", viewCount = 2380),
        PopularDocument(id = 6, documentUUID = "crew-hazel", title = "헤이즐", viewCount = 2257),
        PopularDocument(id = 7, documentUUID = "crew-noel", title = "노엘", viewCount = 2108),
        PopularDocument(id = 8, documentUUID = "crew-summer", title = "서머", viewCount = 1980),
        PopularDocument(id = 9, documentUUID = "crew-dawn", title = "던", viewCount = 1844),
        PopularDocument(id = 10, documentUUID = "crew-mint", title = "민트", viewCount = 1705),
    )

    override fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail? {
        return documentsByUuid[documentId]
    }

    override fun getPopularDocuments(sortType: PopularSortType): List<PopularDocument> {
        return popularDocuments.sortedByDescending { it.viewCount }
    }
}

private fun sampleDocumentDetail(
    documentId: Long,
    uuid: String,
    title: String,
    writer: String,
    organizations: List<String>,
    relatedCrews: List<String>,
    contents: String,
): CrewWikiDocumentDetail {
    return CrewWikiDocumentDetail(
        document = CrewWikiDocument(
            documentId = documentId,
            documentUUID = uuid,
            title = title,
            contents = contents,
            writer = writer,
            generateTime = "2026-06-22T10:54:00",
            organizations = organizations.mapIndexed { index, organization ->
                OrganizationReference(
                    organizationDocumentId = documentId * 100 + index + 1,
                    organizationDocumentUuid = "organization-${documentId}-${index + 1}",
                    title = organization,
                )
            },
        ),
        relatedCrewDocuments = relatedCrews.map { crew ->
            RelatedCrewDocument(
                documentUuid = relatedCrewDocumentUuid(crew),
                title = crew,
            )
        },
    )
}

private fun relatedCrewDocumentUuid(title: String): String {
    return when (title) {
        "비모" -> "sample-document"
        "우디" -> "crew-woody"
        "세인" -> "crew-sain"
        "주디" -> "crew-judy"
        "리버" -> "crew-river"
        "헤이즐" -> "crew-hazel"
        "노엘" -> "crew-noel"
        "서머" -> "crew-summer"
        "던" -> "crew-dawn"
        "민트" -> "crew-mint"
        else -> "sample-document"
    }
}
