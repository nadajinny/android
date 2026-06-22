package com.example.crew_wiki.data.document

import com.example.crew_wiki.model.CrewWikiDocumentDetail

interface DocumentRepository {
    fun getDocumentDetail(documentId: String): CrewWikiDocumentDetail?
}
