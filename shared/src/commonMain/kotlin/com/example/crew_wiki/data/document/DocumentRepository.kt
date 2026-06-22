package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocumentDetail
import com.example.crew_wiki.model.PopularDocument
import com.example.crew_wiki.model.PopularSortType

interface DocumentRepository {
    fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail?

    fun getPopularDocuments(sortType: PopularSortType): List<PopularDocument>
}
